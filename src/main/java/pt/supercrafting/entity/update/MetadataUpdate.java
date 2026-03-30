package pt.supercrafting.entity.update;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pt.supercrafting.entity.type.VirtualBukkitEntity;
import pt.supercrafting.entity.type.VirtualEntity;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

record MetadataUpdate(@Nullable List<@NotNull EntityData<?>> data) implements VirtualEntityUpdate {

    public static VirtualEntityUpdate BUKKIT_ON_THE_FLY = new MetadataUpdate(null);

    @Override
    public @NotNull Collection<PacketWrapper<?>> packets(@NotNull VirtualEntity entity) {

        if (data == null) {
            if (!(entity instanceof VirtualBukkitEntity<?> bukkitEntity))
                throw new IllegalArgumentException("Entity must be a VirtualBukkitEntity");

            List<@NotNull EntityData<?>> data = bukkitEntity.access(SpigotConversionUtil::getEntityMetadata);
            return Collections.singleton(
                    new WrapperPlayServerEntityMetadata(
                            entity.id(),
                            data
                    )
            );
        }

        return Collections.singleton(
                new WrapperPlayServerEntityMetadata(
                        entity.id(),
                        this.data
                )
        );
    }

}
