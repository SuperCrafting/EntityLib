package pt.supercrafting.entity.equipment;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.player.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pt.supercrafting.entity.update.VirtualEntityUpdate;

import java.util.EnumMap;
import java.util.Map;

record EmptyEntityEquipment() implements VirtualEntityEquipment {

    private static final Map<EquipmentSlot, ItemStack> AIR;
    static EmptyEntityEquipment INSTANCE = new EmptyEntityEquipment();

    static {
        Map<EquipmentSlot, ItemStack> map = new EnumMap<>(EquipmentSlot.class);
        for (EquipmentSlot slot : EquipmentSlot.values())
            map.put(slot, null);

        if(PacketEvents.getAPI().getServerManager().getVersion().isOlderThan(ServerVersion.V_1_9))
            map.remove(EquipmentSlot.OFF_HAND);
        AIR = Map.copyOf(map);
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEquipped(@NotNull EquipmentSlot slot) {
        return false;
    }

    @Override
    public @Nullable ItemStack get(@NotNull EquipmentSlot slot) {
        return null;
    }

    @Override
    public @NotNull VirtualEntityUpdate set(@NotNull EquipmentSlot slot, @Nullable ItemStack item) {
        return unset(slot);
    }

    @Override
    public @NotNull VirtualEntityUpdate unset(@NotNull EquipmentSlot slot) {
        return VirtualEntityUpdate.equipment(slot, null);
    }

    @Override
    public @NotNull VirtualEntityUpdate clear() {
        return toUpdate();
    }

    @Override
    public @NotNull VirtualEntityUpdate toUpdate() {
        return VirtualEntityUpdate.equipment(AIR);
    }

}
