package pt.supercrafting.entity.equipment;

import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pt.supercrafting.entity.type.VirtualEntity;
import pt.supercrafting.entity.update.VirtualEntityUpdate;

public interface VirtualEntityEquipment {

    @Nullable
    ItemStack get(@NotNull EquipmentSlot slot);

    @NotNull
    VirtualEntityUpdate set(@NotNull EquipmentSlot slot, @Nullable ItemStack item);

    @NotNull
    static VirtualEntityEquipment create(@NotNull VirtualEntity entity) {
        return new VirtualEntityEquipmentImpl(entity);
    }

}
