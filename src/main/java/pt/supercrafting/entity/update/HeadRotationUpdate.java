package pt.supercrafting.entity.update;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityHeadLook;
import org.jetbrains.annotations.NotNull;
import pt.supercrafting.entity.type.VirtualEntity;

import java.util.Collection;
import java.util.Collections;

record HeadRotationUpdate(float yaw) implements VirtualEntityUpdate {

    @Override
    public @NotNull Collection<PacketWrapper<?>> packets(@NotNull VirtualEntity entity) {
        return Collections.singleton(
                new WrapperPlayServerEntityHeadLook(
                        entity.id(),
                        yaw
                )
        );
    }

}
