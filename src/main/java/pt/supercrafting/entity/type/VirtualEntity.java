package pt.supercrafting.entity.type;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.*;
import pt.supercrafting.entity.equipment.VirtualEntityEquipment;
import pt.supercrafting.entity.interaction.VirtualEntityInteractionHolder;
import pt.supercrafting.entity.tick.VirtualEntityTickingAction;
import pt.supercrafting.entity.tick.VirtualEntityTickingActionHolder;
import pt.supercrafting.entity.update.VirtualEntitySpawn;
import pt.supercrafting.entity.update.VirtualEntityUpdate;
import pt.supercrafting.entity.visibility.VirtualEntityVisibility;

import java.util.Collection;

public sealed interface VirtualEntity permits VirtualBukkitEntity, VirtualEntityImpl, VirtualHumanEntity {

    @ApiStatus.Internal
    @Contract("_, _, _ -> new")
    static @NotNull VirtualEntity create(final int id, @NotNull EntityType type, @NotNull Location location) {
        return new VirtualEntityImpl(id, type, location);
    }

    int id();

    @NotNull
    VirtualEntitySpawn spawn();

    @NotNull
    VirtualEntitySpawn destroy();

    @NotNull
    VirtualEntityInteractionHolder interactions();

    @NotNull
    VirtualEntityTickingActionHolder tickingActions();

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

    default void update(@NotNull VirtualEntityUpdate update) {
        update(update, null);
    }

    void update(@NotNull VirtualEntityUpdate update, @Nullable Collection<Player> viewers);

}
