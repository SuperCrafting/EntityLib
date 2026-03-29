package pt.supercrafting.entity.type;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.google.common.collect.Lists;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import pt.supercrafting.entity.equipment.VirtualEntityEquipment;
import pt.supercrafting.entity.interaction.VirtualEntityInteraction;
import pt.supercrafting.entity.tick.TickingAction;
import pt.supercrafting.entity.update.VirtualEntityUpdate;
import pt.supercrafting.entity.visibility.VirtualEntityVisibility;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

sealed class VirtualEntityImpl implements VirtualEntity permits VirtualBukkitEntityImpl, VirtualHumanEntityImpl {

    private final int id;
    private final EntityType type;
    private boolean valid = true;

    private Location location;
    private final VirtualEntityEquipment equipment = VirtualEntityEquipment.create(this);
    private final VirtualEntityVisibility visibility = VirtualEntityVisibility.create(this);

    private final Collection<@NotNull VirtualEntityInteraction> interactions;
    private final Collection<@NotNull TickingAction> actions;

    private final VirtualEntityPacketFactory packetFactory;

    public VirtualEntityImpl(int id, @NotNull EntityType type, @NotNull Location location) {
        this.id = id;
        this.type = Objects.requireNonNull(type, "type cannot be null");
        this.interactions = Lists.newArrayList();
        this.actions = Lists.newArrayList();
        this.location(location);
        this.packetFactory = packetFactory();
    }

    @NotNull
    @Override
    public VirtualEntityPacketFactory packetFactory() {
        return new VirtualEntityPacketFactory.FallBack(this);
    }

    @Override
    public @UnmodifiableView Collection<@NotNull VirtualEntityInteraction> interactions() {
        return List.copyOf(this.interactions);
    }

    @Override
    public void registerInteraction(final @NotNull VirtualEntityInteraction interaction) {
        this.interactions.add(interaction);
    }

    @Override
    public void unregisterInteraction(final @NotNull VirtualEntityInteraction interaction) {
        this.interactions.remove(interaction);
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public @UnmodifiableView Collection<@NotNull TickingAction> tickingActions() {
        return List.copyOf(this.actions);
    }

    @Override
    public void registerTickingAction(final @NotNull TickingAction action) {
        this.actions.add(action);
    }

    @Override
    public void unregisterTickingAction(final @NotNull TickingAction action) {
        this.actions.remove(action);
    }

    @Override
    public @NotNull EntityType type() {
        return type;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public void remove() {
        if (!valid)
            return;

        valid = false;
    }

    @Override
    public @NotNull Location location() {
        return location.clone();
    }

    @Override
    public @NotNull World world() {
        return location.getWorld();
    }

    @Override
    public void location(@NotNull Location location) {
        this.location = Objects.requireNonNull(location, "location cannot be null");
    }

    @Override
    public @NotNull VirtualEntityEquipment equipment() {
        return equipment;
    }

    @Override
    public @NotNull VirtualEntityVisibility visibility() {
        return visibility;
    }

    @Override
    public void update(@NotNull VirtualEntityUpdate update, @Nullable Collection<Player> viewers) {
        if (viewers == null) {
            viewers = this.visibility.viewers();
        }

        if (viewers.isEmpty()) {
            return;
        }

        Collection<PacketWrapper<?>> packets = update.packets(this);
        if (packets.isEmpty()) {
            return;
        }

        for (PacketWrapper<?> packet : packets) {
            for (Player viewer : viewers) {
                PacketEvents.getAPI().getPlayerManager().sendPacket(viewer, packet);
                viewer.sendMessage("Packet sent: " + packet.getClass().getSimpleName());
            }
        }
    }

}
