package pt.supercrafting.entity.type;

import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

interface VirtualEntityPacketFactory {

    Collection<PacketWrapper<?>> spawn();
    Collection<PacketWrapper<?>> destroy();

    record Default(@NotNull VirtualEntity entity) implements VirtualEntityPacketFactory {

        public Default(@NotNull VirtualEntity entity) {
            this.entity = Objects.requireNonNull(entity, "entity cannot be null");
        }

        @Override
        public Collection<PacketWrapper<?>> spawn() {
            Location location = entity.location();
            return Collections.singleton(
                    new WrapperPlayServerSpawnEntity(
                            entity.id(),
                            null,
                            entity.type(),
                            SpigotConversionUtil.fromBukkitLocation(location),
                            location.getYaw(),
                            0,
                            new Vector3d()
                    )
            );
        }

        @Override
        public Collection<PacketWrapper<?>> destroy() {
            return Collections.singleton(
                    new WrapperPlayServerDestroyEntities(entity.id())
            );
        }

    }

}
