package pt.supercrafting.entity.update;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityTeleport;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import pt.supercrafting.entity.type.VirtualEntity;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

record TeleportUpdate(@NotNull Location location, boolean onGround) implements VirtualEntityUpdate {

    TeleportUpdate(@NotNull Location location, boolean onGround) {
        this.location = Objects.requireNonNull(location, "location cannot be null").clone();
        this.onGround = onGround;
    }

    @Override
    public @NotNull Collection<PacketWrapper<?>> packets(@NotNull VirtualEntity entity) {
        return Collections.singleton(
                new WrapperPlayServerEntityTeleport(
                        entity.id(),
                        SpigotConversionUtil.fromBukkitLocation(this.location),
                        onGround
                )
        );
    }

}
