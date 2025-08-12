package pt.supercrafting.entity.type;

import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pt.supercrafting.entity.equipment.VirtualEntityEquipment;
import pt.supercrafting.entity.update.VirtualEntityUpdate;
import pt.supercrafting.entity.visibility.VirtualEntityVisibility;

import java.util.Collection;

public sealed interface VirtualEntity permits VirtualBukkitEntity, VirtualEntityImpl, VirtualHumanEntity {

    int id();

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
