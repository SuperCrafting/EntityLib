package pt.supercrafting.entity.equipment;

import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pt.supercrafting.entity.update.VirtualEntityUpdate;

public interface VirtualEntityEquipment {

    @ApiStatus.Internal
    @NotNull
    static VirtualEntityEquipment create() {
        return new VirtualEntityEquipmentImpl();
    }

    @NotNull
    static VirtualEntityEquipment empty() {
        return EmptyEntityEquipment.INSTANCE;
    }

    boolean isEmpty();

    int size();

    boolean isEquipped(@NotNull EquipmentSlot slot);

    @Nullable
    ItemStack get(@NotNull EquipmentSlot slot);

    @NotNull
    VirtualEntityUpdate set(@NotNull EquipmentSlot slot, @Nullable ItemStack item);

    @NotNull
   default VirtualEntityUpdate unset(@NotNull EquipmentSlot slot) {
        return set(slot, null);
    }

    @NotNull
    VirtualEntityUpdate clear();

    @NotNull
    VirtualEntityUpdate toUpdate();

}
