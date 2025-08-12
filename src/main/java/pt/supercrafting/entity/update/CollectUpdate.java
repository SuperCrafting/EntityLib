package pt.supercrafting.entity.update;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCollectItem;
import org.jetbrains.annotations.NotNull;
import pt.supercrafting.entity.type.VirtualEntity;

import java.util.Collection;
import java.util.Collections;

record CollectUpdate(int pickedId, int count) implements VirtualEntityUpdate {

    public CollectUpdate(int pickedId, int count) {
        this.pickedId = pickedId;
        this.count = count;
    }

    @Override
    public @NotNull Collection<PacketWrapper<?>> packets(@NotNull VirtualEntity entity) {
        return Collections.singleton(
                new WrapperPlayServerCollectItem(
                        entity.id(),
                        pickedId,
                        count
                )
        );
    }

}
