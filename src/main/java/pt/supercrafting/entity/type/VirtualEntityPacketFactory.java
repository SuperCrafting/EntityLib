package pt.supercrafting.entity.type;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnLivingEntity;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import pt.supercrafting.entity.equipment.VirtualEntityEquipment;
import pt.supercrafting.entity.update.VirtualEntityUpdate;

import java.util.*;

public interface VirtualEntityPacketFactory {

    Collection<PacketWrapper<?>> spawn();
    Collection<PacketWrapper<?>> destroy();

    Collection<EntityData<?>> dataWatcher();

    class FallBack implements VirtualEntityPacketFactory {

        private final VirtualEntity entity;

        public FallBack(@NotNull VirtualEntity entity) {
            this.entity = Objects.requireNonNull(entity, "entity cannot be null");
        }

        @Override
        public Collection<EntityData<?>> dataWatcher() {
            return Collections.emptySet();
        }

        @Override
        public Collection<PacketWrapper<?>> spawn() {

            Location location = entity.location();
            boolean living = EntityTypes.isTypeInstanceOf(entity.type(), EntityTypes.LIVINGENTITY);
            boolean item = EntityTypes.ITEM.equals(entity.type());
            List<EntityData<?>> dataWatcher = new ArrayList<>(dataWatcher());

            List<PacketWrapper<?>> packets = new ArrayList<>(8);
            if (living) {
                packets.add(new WrapperPlayServerSpawnLivingEntity(
                        entity.id(),
                        null,
                        entity.type(),
                        SpigotConversionUtil.fromBukkitLocation(location),
                        0,
                        new Vector3d(),
                        dataWatcher
                ));
            } else {
                packets.add(new WrapperPlayServerSpawnEntity(
                        entity.id(),
                        null,
                        entity.type(),
                        SpigotConversionUtil.fromBukkitLocation(location),
                        location.getYaw(),
                        item ? 2 : 0,
                        new Vector3d()
                ));
            }

            if(living)
                packets.addAll(VirtualEntityUpdate.headRotation(location.getYaw()).packets(entity));

            if((living || item) && !dataWatcher.isEmpty())
                packets.add(new WrapperPlayServerEntityMetadata(
                        entity.id(),
                        dataWatcher
                ));

            VirtualEntityEquipment equipment = entity.equipment();
            if(!equipment.isEmpty())
                packets.addAll(equipment.toUpdate().packets(entity));

            return packets;
        }

        @Override
        public Collection<PacketWrapper<?>> destroy() {
            return Collections.singleton(new WrapperPlayServerDestroyEntities(entity.id()));
        }
    }
}
