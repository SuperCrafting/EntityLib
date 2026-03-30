package pt.supercrafting.entity.update;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityAnimation;
import org.jetbrains.annotations.NotNull;
import pt.supercrafting.entity.type.VirtualEntity;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

record AnimationUpdate(
        @NotNull WrapperPlayServerEntityAnimation.EntityAnimationType type) implements VirtualEntityUpdate {

    AnimationUpdate(@NotNull WrapperPlayServerEntityAnimation.EntityAnimationType type) {
        this.type = Objects.requireNonNull(type, "type cannot be null");
    }

    @Override
    public @NotNull Collection<PacketWrapper<?>> packets(@NotNull VirtualEntity entity) {
        return Collections.singleton(
                new WrapperPlayServerEntityAnimation(
                        entity.id(),
                        type
                )
        );
    }

}
