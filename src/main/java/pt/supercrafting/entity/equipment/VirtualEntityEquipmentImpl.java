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
    public boolean isEmpty() {
        if(this.handle.isEmpty())
            return true;

        for (@Nullable ItemStack item : this.handle.values())
            if(item != null)
                return false;
        return true;
    }

    @Override
    public int size() {
        return this.handle.size();
    }

    @Override
    public boolean isEquipped(@NotNull EquipmentSlot slot) {
        return handle.containsKey(slot) && handle.get(slot) != null;
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
        if(item != null)
            handle.put(slot, item);
        else
            handle.remove(slot);
        return VirtualEntityUpdate.equipment(slot, item);
    }

    @Override
    public @NotNull VirtualEntityUpdate unset(@NotNull EquipmentSlot slot) {
        return set(slot, null);
    }

    @Override
    public @NotNull VirtualEntityUpdate clear() {
        this.handle.clear();
        return toUpdate();
    }

    @Override
    public @NotNull VirtualEntityUpdate toUpdate() {
        return VirtualEntityUpdate.equipment(this.handle);
    }

}
