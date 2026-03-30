package pt.supercrafting.entity;

import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;
import pt.supercrafting.entity.type.VirtualBukkitEntity;
import pt.supercrafting.entity.type.VirtualEntity;
import pt.supercrafting.entity.type.VirtualHumanEntity;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public sealed interface EntityLib permits EntityLibImpl {

    @NotNull
    static EntityLib create(@NotNull Plugin plugin, @NotNull PacketEventsAPI<?> packetEvents) {
        return new EntityLibImpl(plugin, packetEvents);
    }

    void destroy();

    @NotNull
    @UnmodifiableView
    Collection<VirtualEntity> entities();

    @NotNull
    Optional<VirtualEntity> entityById(int id);

    void registerEntity(@NotNull VirtualEntity entity);

    default void unregisterEntity(@NotNull VirtualEntity entity) {
        unregisterEntity(Objects.requireNonNull(entity, "entity cannot be null").id());
    }

    void unregisterEntity(int id);

    @NotNull
    VirtualEntity createEntity(@NotNull Location location, @NotNull EntityType entityType);

    @NotNull
    <E extends Entity> VirtualBukkitEntity<E> createBukkit(@NotNull Location location, @NotNull Class<E> type);

    @NotNull
    VirtualHumanEntity createHuman(@NotNull Location location);

}