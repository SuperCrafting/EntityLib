package pt.supercrafting.entity.update;

import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityVelocity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import pt.supercrafting.entity.type.VirtualEntity;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

record VelocityUpdate(@NotNull Vector velocity) implements VirtualEntityUpdate {

    public VelocityUpdate(@NotNull Vector velocity) {
        this.velocity = Objects.requireNonNull(velocity, "velocity cannot be null").clone();
    }

    @Override
    public @NotNull Collection<PacketWrapper<?>> packets(@NotNull VirtualEntity entity) {
        return Collections.singleton(
                new WrapperPlayServerEntityVelocity(
                        entity.id(),
                        new Vector3d(
                                velocity.getX(),
                                velocity.getY(),
                                velocity.getZ()
                        )
                )
        );
    }

}
