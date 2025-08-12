package pt.supercrafting.entity.equipment;

import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pt.supercrafting.entity.type.VirtualEntity;
import pt.supercrafting.entity.update.VirtualEntityUpdate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

record VirtualEntityEquipmentImpl(@NotNull VirtualEntity entity, @NotNull Map<@NotNull EquipmentSlot, @Nullable ItemStack> handle) implements VirtualEntityEquipment {

    private static final int SIZE = EquipmentSlot.values().length;

    public VirtualEntityEquipmentImpl(@NotNull VirtualEntity entity) {
        this(entity, new HashMap<>(SIZE));
    }

    VirtualEntityEquipmentImpl(@NotNull VirtualEntity entity, @NotNull Map<@NotNull EquipmentSlot, @Nullable ItemStack> handle) {
        this.entity = Objects.requireNonNull(entity, "entity cannot be null");
        this.handle = Objects.requireNonNull(handle, "handle cannot be null");
    }

    @Override
    public @Nullable ItemStack get(@NotNull EquipmentSlot slot) {
        Objects.requireNonNull(slot, "slot cannot be null");
        return handle.get(slot);
    }

    @NotNull
    @Override
    public VirtualEntityUpdate set(@NotNull EquipmentSlot slot, @Nullable ItemStack item) {
        Objects.requireNonNull(slot, "slot cannot be null");
        handle.put(slot, item);
        return VirtualEntityUpdate.equipment(slot, item);
    }

}
