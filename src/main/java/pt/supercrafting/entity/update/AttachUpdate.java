package pt.supercrafting.entity.update;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerAttachEntity;
import org.jetbrains.annotations.NotNull;
import pt.supercrafting.entity.type.VirtualEntity;

import java.util.Collection;
import java.util.Collections;

record AttachUpdate(int passengerId, boolean leash) implements VirtualEntityUpdate {

    @Override
    public @NotNull Collection<PacketWrapper<?>> packets(@NotNull VirtualEntity entity) {
        return Collections.singleton(
                new WrapperPlayServerAttachEntity(
                        entity.id(),
                        passengerId,
                        leash
                )
        );
    }

}
