package pt.supercrafting.entity.update;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRelativeMove;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityRelativeMoveAndRotation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import pt.supercrafting.entity.type.VirtualEntity;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

record MoveUpdate(@NotNull Vector move, float yaw, float pitch, boolean onGround) implements VirtualEntityUpdate {

    MoveUpdate(@NotNull Vector move, float yaw, float pitch, boolean onGround) {
        this.move = Objects.requireNonNull(move, "move cannot be null").clone();
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    @Override
    public @NotNull Collection<PacketWrapper<?>> packets(@NotNull VirtualEntity entity) {

        if(yaw == 0 && pitch == 0)
            return Collections.singleton(
                    new WrapperPlayServerEntityRelativeMove(
                            entity.id(),
                            move.getX(),
                            move.getY(),
                            move.getZ(),
                            onGround
                    )
            );

        return Collections.singleton(
                new WrapperPlayServerEntityRelativeMoveAndRotation(
                        entity.id(),
                        move.getX(),
                        move.getY(),
                        move.getZ(),
                        pitch,
                        yaw,
                        onGround
                )
        );
    }

}
