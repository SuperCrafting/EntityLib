package pt.supercrafting.entity.equipment;

import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pt.supercrafting.entity.type.VirtualEntity;
import pt.supercrafting.entity.update.VirtualEntityUpdate;

public interface VirtualEntityEquipment {

    @ApiStatus.Internal
    @NotNull
    static VirtualEntityEquipment create(@NotNull VirtualEntity entity) {
        return new VirtualEntityEquipmentImpl(entity);
    }

    boolean isEmpty();

    int size();

    boolean isEquipped(@NotNull EquipmentSlot slot);

    @Nullable
    ItemStack get(@NotNull EquipmentSlot slot);

    @NotNull
    VirtualEntityUpdate set(@NotNull EquipmentSlot slot, @Nullable ItemStack item);

    @NotNull
    VirtualEntityUpdate unset(@NotNull EquipmentSlot slot);

    @NotNull
    VirtualEntityUpdate clear();

    @NotNull
    VirtualEntityUpdate toUpdate();

}
