package pt.supercrafting.entity.update;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.player.Equipment;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEquipment;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pt.supercrafting.entity.type.VirtualEntity;

import java.util.*;

record EquipmentUpdate(@NotNull Map<@NotNull EquipmentSlot, @Nullable ItemStack> equipment) implements VirtualEntityUpdate {

    EquipmentUpdate(@NotNull Map<@NotNull EquipmentSlot, @Nullable ItemStack> equipment) {
        this.equipment = Objects.requireNonNull(equipment, "equipment cannot be null");
    }

    @Override
    public @NotNull Collection<PacketWrapper<?>> packets(@NotNull VirtualEntity entity) {

        if(!supportMultiEquipment())
            return Collections.singleton(
                    new WrapperPlayServerEntityEquipment(
                            entity.id(),
                            toEquipments()
                    )
            );

        List<PacketWrapper<?>> packets = new ArrayList<>(this.equipment.size());
        for (Map.Entry<@NotNull EquipmentSlot, @Nullable ItemStack> entry : this.equipment.entrySet())
            packets.add(
                    new WrapperPlayServerEntityEquipment(
                            entity.id(),
                            Collections.singletonList(toEquipment(entry))
                    )
            );
        return packets;
    }

    public boolean supportMultiEquipment() {
        return equipment.size() > 1 && PacketEvents.getAPI().getServerManager().getVersion().isNewerThanOrEquals(ServerVersion.V_1_16);
    }

    @NotNull
    public static Equipment toEquipment(@NotNull Map.Entry<@NotNull EquipmentSlot, @Nullable ItemStack> entry) {
        EquipmentSlot slot = entry.getKey();
        ItemStack item = entry.getValue();
        return new Equipment(
                slot,
                SpigotConversionUtil.fromBukkitItemStack(item)
        );
    }

    @NotNull
    public List<Equipment> toEquipments() {
        List<Equipment> equipments = new ArrayList<>(this.equipment.size());
        for (Map.Entry<@NotNull EquipmentSlot, @Nullable ItemStack> entry : this.equipment.entrySet()) {
            Equipment equipment = toEquipment(entry);
            equipments.add(equipment);
        }
        return equipments;
    }

}
