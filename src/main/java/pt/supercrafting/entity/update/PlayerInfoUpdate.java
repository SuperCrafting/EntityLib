package pt.supercrafting.entity.update;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo;
import org.jetbrains.annotations.NotNull;
import pt.supercrafting.entity.type.VirtualEntity;
import pt.supercrafting.entity.type.VirtualHumanEntity;

import java.util.Collection;
import java.util.Collections;

record PlayerInfoUpdate(WrapperPlayServerPlayerInfo.Action action) implements VirtualEntityUpdate {

    @Override
    public @NotNull Collection<PacketWrapper<?>> packets(@NotNull final VirtualEntity entity) {
        if (!(entity instanceof final VirtualHumanEntity human))
            return Collections.emptyList();
        return Collections.singleton(new WrapperPlayServerPlayerInfo(this.action, human.toPlayerData()));
    }

}
