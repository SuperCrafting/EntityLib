package pt.supercrafting.entity.type;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public final class VirtualBukkitEntityImpl<E extends Entity> extends VirtualEntityImpl implements VirtualBukkitEntity<E> {

    private final E handle;

    public VirtualBukkitEntityImpl(@NotNull E entity) {
        super(Objects.requireNonNull(entity, "entity cannot be null").getEntityId(), EntityTypes.getById(
                PacketEvents.getAPI().getServerManager().getVersion().toClientVersion(),
                entity.getType().getTypeId()
        ), entity.getLocation());
        this.handle = entity;
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

}
