package pt.supercrafting.entity;

import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import pt.supercrafting.entity.type.VirtualBukkitEntity;
import pt.supercrafting.entity.type.VirtualEntity;
import pt.supercrafting.entity.type.VirtualHumanEntity;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

final class EntityLibImpl extends PacketListenerAbstract implements EntityLib, Runnable {

    private static final MethodHandle CREATE_ENTITY_METHOD;

    private static final MethodHandle ITEM_AS_NMS_COPY;
    private static final MethodHandle ITEM_GET_WORLD_HANDLE;
    private static final MethodHandle ITEM_GET_BUKKIT_ENTITY;
    private static final MethodHandle ITEM_ENTITY_CONSTRUCTOR;

    static {
        try {
            final MethodHandles.Lookup lookup = MethodHandles.lookup();

            Class<?> craftRegionAccessorClass = SpigotReflectionUtil.getOBCClass("CraftRegionAccessor");
            Class<?> targetClass = craftRegionAccessorClass != null
                    ? craftRegionAccessorClass
                    : SpigotReflectionUtil.CRAFT_WORLD_CLASS;

            Method method = targetClass.getDeclaredMethod("createEntity",
                    Location.class,
                    Class.class
            );

            method.setAccessible(true);

            CREATE_ENTITY_METHOD = lookup.unreflect(method);

            ITEM_AS_NMS_COPY = lookup.unreflect(SpigotReflectionUtil.CRAFT_ITEM_STACK_AS_NMS_COPY);
            ITEM_GET_WORLD_HANDLE = lookup.unreflect(SpigotReflectionUtil.GET_CRAFT_WORLD_HANDLE_METHOD);
            ITEM_GET_BUKKIT_ENTITY = lookup.unreflect(SpigotReflectionUtil.GET_BUKKIT_ENTITY_METHOD);

            Class<?> entityItemClass = resolveEntityItemClass();
            ITEM_ENTITY_CONSTRUCTOR = lookup.findConstructor(
                    entityItemClass,
                    MethodType.methodType(void.class,
                            SpigotReflectionUtil.LEVEL_CLASS,
                            double.class, double.class, double.class,
                            SpigotReflectionUtil.NMS_ITEM_STACK_CLASS
                    )
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize EntityLib", e);
        }
    }

    private static Class<?> resolveEntityItemClass() {
        Class<?> cls = SpigotReflectionUtil.getServerClass("world.entity.item.ItemEntity", "EntityItem");
        if (cls == null)
            cls = SpigotReflectionUtil.getServerClass("world.entity.item.EntityItem", "EntityItem");
        if (cls == null)
            throw new IllegalStateException("Could not resolve EntityItem / ItemEntity class");
        return cls;
    }

    private final PacketEventsAPI<?> packetEvents;

    private final BukkitTask task;
    private final Map<Integer, VirtualEntity> entities; // Todo: use fast util Int2ObjectMap -> shade fastutil
    private int currentTick = 0;
    private Collection<VirtualEntity> entitiesView;

    public EntityLibImpl(@NotNull final Plugin plugin, @NotNull PacketEventsAPI<?> packetEvents) {
        Objects.requireNonNull(plugin, "plugin cannot be null");
        this.packetEvents = Objects.requireNonNull(packetEvents, "packetEvents cannot be null");
        this.entities = new ConcurrentHashMap<>();

        packetEvents.getEventManager().registerListener(this);
        this.task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(
                plugin,
                this,
                0L,
                1L
        );
    }

    @Override
    public void run() {
        for (final var entity : this.entities()) {
            if (!entity.isValid()) {
                unregisterEntity(entity);
                continue;
            }

            entity.tickingActions().onTick(currentTick);
            entity.visibility().run();
        }

        this.currentTick++;
    }

    @Override
    public void onPacketReceive(final @NotNull PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.INTERACT_ENTITY) {

            User user = event.getUser();
            Player player = Bukkit.getPlayer(user.getUUID());
            if (player == null || !player.isOnline())
                return;

            final var packet = new WrapperPlayClientInteractEntity(event);
            this.entityById(packet.getEntityId()).ifPresent(entity -> {
                entity.interactions().onInteract(player, entity, packet.getAction());
            });
        }
    }

    @Override
    public void destroy() {
        this.task.cancel();
        for (VirtualEntity entity : this.entities.values())
            entity.remove();
        this.entities.clear();

        this.packetEvents.getEventManager().unregisterListener(this);
    }

    @UnmodifiableView
    @NotNull
    @Override
    public Collection<VirtualEntity> entities() {
        if (entitiesView == null)
            entitiesView = Collections.unmodifiableCollection(entities.values());
        return this.entitiesView;
    }

    @NotNull
    @Override
    public Optional<VirtualEntity> entityById(final int id) {
        return Optional.ofNullable(this.entities.get(id));
    }

    @Override
    public void registerEntity(@NotNull VirtualEntity entity) {
        this.entities.put(Objects.requireNonNull(entity, "entity cannot be null").id(), entity);
    }

    @Override
    public void unregisterEntity(final int id) {
        this.entities.remove(id);
    }

    @NotNull
    @Override
    public VirtualEntity createEntity(@NotNull final Location location, @NotNull final EntityType entityType) {
        Objects.requireNonNull(location, "location cannot be null");
        Objects.requireNonNull(entityType, "entityType cannot be null");

        if (entityType == EntityTypes.PLAYER) {
            return this.createHuman(location);
        }

        final var entity = VirtualEntity.create(
                SpigotReflectionUtil.generateEntityId(),
                entityType,
                location
        );
        registerEntity(entity);
        return entity;
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public <E extends Entity> VirtualBukkitEntity<E> createBukkit(@NotNull Location location, final @NotNull Class<E> type) {
        try {
            Object nms = CREATE_ENTITY_METHOD.invoke(location.getWorld(), location, type);
            E bukkit = (E) SpigotReflectionUtil.getBukkitEntity(nms);
            VirtualBukkitEntity<E> entity = VirtualBukkitEntity.create(bukkit);
            registerEntity(entity);
            return entity;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    @Override
    public VirtualHumanEntity createHuman(@NotNull final Location location) {
        final var human = VirtualHumanEntity.create(
                SpigotReflectionUtil.generateEntityId(),
                Objects.requireNonNull(location, "location cannot be null")
        );
        registerEntity(human);
        return human;
    }

    @NotNull
    @Override
    public VirtualBukkitEntity<Item> createItem(@NotNull final Location location, @NotNull final ItemStack itemStack) {
        Objects.requireNonNull(location, "location cannot be null");
        Objects.requireNonNull(itemStack, "itemStack cannot be null");
        if (location.getWorld() == null) throw new IllegalArgumentException("location has no world");

        try {
            Object nmsItem = ITEM_AS_NMS_COPY.invoke(itemStack);
            Object nmsWorld = ITEM_GET_WORLD_HANDLE.invoke(location.getWorld());
            Object nmsEntity = ITEM_ENTITY_CONSTRUCTOR.invoke(nmsWorld, location.getX(), location.getY(), location.getZ(), nmsItem);
            Item item = (Item) ITEM_GET_BUKKIT_ENTITY.invoke(nmsEntity);
            VirtualBukkitEntity<Item> entity = VirtualBukkitEntity.create(item);
            registerEntity(entity);
            return entity;
        } catch (Throwable t) {
            throw new RuntimeException("Failed to create item entity", t);
        }
    }

}
