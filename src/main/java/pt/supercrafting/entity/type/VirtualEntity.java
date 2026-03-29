package pt.supercrafting.entity.type;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import pt.supercrafting.entity.equipment.VirtualEntityEquipment;
import pt.supercrafting.entity.interaction.VirtualEntityInteraction;
import pt.supercrafting.entity.interaction.VirtualEntityInteractionHolder;
import pt.supercrafting.entity.tick.TickingAction;
import pt.supercrafting.entity.tick.TickingActionHolder;
import pt.supercrafting.entity.update.VirtualEntityUpdate;
import pt.supercrafting.entity.visibility.VirtualEntityVisibility;

import java.util.Collection;

public sealed interface VirtualEntity extends TickingActionHolder, VirtualEntityInteractionHolder permits VirtualBukkitEntity, VirtualEntityImpl, VirtualHumanEntity {

    @Contract("_, _, _ -> new")
    static @NotNull VirtualEntity create(final int id, @NotNull EntityType type, @NotNull Location location) {
        return new VirtualEntityImpl(id, type, location);
    }

    int id();

    @NotNull VirtualEntityPacketFactory packetFactory();

    @Override
    @UnmodifiableView
    Collection<@NotNull VirtualEntityInteraction> interactions();

    @Override
    void registerInteraction(final @NotNull VirtualEntityInteraction interaction);

    @Override
    void unregisterInteraction(final @NotNull VirtualEntityInteraction interaction);

    @Override
    @UnmodifiableView
    Collection<@NotNull TickingAction> tickingActions();

    @Override
    void registerTickingAction(final @NotNull TickingAction action);

    @Override
    void unregisterTickingAction(final @NotNull TickingAction action);

    @NotNull
    EntityType type();

    boolean isValid();

    void remove();

    @Contract("-> new")
    @NotNull
    Location location();

    @NotNull
    World world();

    void location(@NotNull Location location);

    @NotNull
    VirtualEntityVisibility visibility();

    @NotNull
    VirtualEntityEquipment equipment();

    default void onSpawn(final Player player) {

    }

    default void update(@NotNull VirtualEntityUpdate update) {
        update(update, null);
    }

    void update(@NotNull VirtualEntityUpdate update, @Nullable Collection<Player> viewers);

}
