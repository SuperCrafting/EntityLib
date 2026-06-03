package pt.supercrafting.entity.type;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pt.supercrafting.entity.equipment.VirtualEntityEquipment;
import pt.supercrafting.entity.update.VirtualEntityUpdate;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

final class VirtualBukkitEntityImpl<E extends Entity> extends VirtualEntityImpl implements VirtualBukkitEntity<E> {

    private final E handle;

    public VirtualBukkitEntityImpl(@NotNull E entity) {
        super(Objects.requireNonNull(entity, "entity cannot be null").getEntityId(), SpigotConversionUtil.fromBukkitEntityType(entity.getType()), entity.getLocation());
        this.handle = entity;
    }

    @Override
    protected @NotNull VirtualEntityPacketFactory packetFactory() {
        return new PacketFactory();
    }

    @Override
    protected VirtualEntityEquipment createEquipment() {
        return handle instanceof LivingEntity livingEntity ? new Equipment(livingEntity.getEquipment()) : VirtualEntityEquipment.empty();
    }

    @Override
    public void modify(@NotNull Consumer<@NotNull E> consumer) {
        Objects.requireNonNull(consumer, "consumer cannot be null");
        consumer.accept(this.handle);
    }

    @Override
    public <T> T access(@NotNull Function<@NotNull E, T> function) {
        Objects.requireNonNull(function, "function cannot be null");
        return function.apply(this.handle);
    }

    private final class PacketFactory extends VirtualEntityPacketFactory.FallBack {

        public PacketFactory() {
            super(VirtualBukkitEntityImpl.this);
        }

        @Override
        public Collection<EntityData<?>> dataWatcher() {
            return SpigotConversionUtil.getEntityMetadata(handle);
        }

    }

    private record Equipment(EntityEquipment handle) implements VirtualEntityEquipment {

        private static final boolean OFFHAND =
                    PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_9);

        private static final List<EquipmentSlot> SUPPORTED_SLOTS;

        static {
            List<EquipmentSlot> slots = new ArrayList<>(List.of(
                    EquipmentSlot.HELMET,
                    EquipmentSlot.CHEST_PLATE,
                    EquipmentSlot.LEGGINGS,
                    EquipmentSlot.BOOTS
            ));
            if(OFFHAND)
                slots.add(EquipmentSlot.OFF_HAND);
            SUPPORTED_SLOTS = List.copyOf(slots);
        }

        @Override
        public boolean isEmpty() {
            return asMap(false).isEmpty();
        }

        @Override
        public int size() {
            return asMap(false).size();
        }

        @Override
        public boolean isEquipped(@NotNull EquipmentSlot slot) {
            ItemStack at = get(slot);
            return at != null && at.getType() != Material.AIR; // Todo: newer versions have isEmpty or getType().isAir()
        }

        @Override
        public @Nullable ItemStack get(@NotNull EquipmentSlot slot) {
            return switch (slot) {
                case HELMET -> handle.getHelmet();
                case CHEST_PLATE -> handle.getChestplate();
                case LEGGINGS -> handle.getLeggings();
                case BOOTS -> handle.getBoots();
                case OFF_HAND -> OFFHAND ? handle.getItemInOffHand() : null;
                default -> throw new IllegalArgumentException("Unsupported equipment slot: " + slot);
            };
        }

        @Override
        public @NotNull VirtualEntityUpdate set(@NotNull EquipmentSlot slot, @Nullable ItemStack item) {
            switch (slot) {
                case HELMET -> handle.setHelmet(item);
                case CHEST_PLATE -> handle.setChestplate(item);
                case LEGGINGS -> handle.setLeggings(item);
                case BOOTS -> handle.setBoots(item);
                case OFF_HAND -> {
                    if (OFFHAND) handle.setItemInOffHand(item);
                    else throw new IllegalArgumentException("Off-hand equipment is not supported in this server version");
                }
                default -> throw new IllegalArgumentException("Unsupported equipment slot: " + slot);
            }
            return VirtualEntityUpdate.equipment(slot, item);
        }

        @Override
        public @NotNull VirtualEntityUpdate clear() {
            Map<EquipmentSlot, ItemStack> map = asMap(false);
            for (Map.Entry<EquipmentSlot, ItemStack> entry : map.entrySet())
                unset(entry.getKey());
            return toUpdate();
        }

        @Override
        public @NotNull VirtualEntityUpdate toUpdate() {
            return VirtualEntityUpdate.equipment(asMap(true));
        }

        private Map<EquipmentSlot, ItemStack> asMap(boolean allowNull) {
            Map<EquipmentSlot, ItemStack> map = new EnumMap<>(EquipmentSlot.class);
            for (EquipmentSlot slot : SUPPORTED_SLOTS) {
                ItemStack item = get(slot);
                if (allowNull || (item != null && item.getType() != Material.AIR))
                    map.put(slot, item);
            }
            return map;
        }

        private Optional<ItemStack> optional(Function<EntityEquipment, ItemStack> function) {
            return Optional.ofNullable(function.apply(handle));
        }

    }

}
