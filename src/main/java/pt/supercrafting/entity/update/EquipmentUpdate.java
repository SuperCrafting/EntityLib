package pt.supercrafting.entity.update;

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
        return Collections.singleton(
                new WrapperPlayServerEntityEquipment(
                        entity.id(),
                        toEquipments()
                )
        );
    }

    @NotNull
    public List<Equipment> toEquipments() {
        List<Equipment> equipments = new ArrayList<>(this.equipment.size());
        for (Map.Entry<@NotNull EquipmentSlot, @Nullable ItemStack> entry : this.equipment.entrySet()) {
            ItemStack itemStack = entry.getValue();
            EquipmentSlot slot = entry.getKey();

            Equipment equipment = new Equipment(
                    slot,
                    SpigotConversionUtil.fromBukkitItemStack(itemStack)
            );
            equipments.add(equipment);
        }
        return equipments;
    }

}
