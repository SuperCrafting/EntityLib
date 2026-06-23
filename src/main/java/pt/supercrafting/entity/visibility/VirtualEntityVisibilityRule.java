package pt.supercrafting.entity.visibility;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pt.supercrafting.entity.type.VirtualEntity;

public interface VirtualEntityVisibilityRule {

    @NotNull
    static VirtualEntityVisibilityRule always() {
        return (entity, player) ->
                player.getWorld() == entity.world() && player.getLocation().distance(entity.location()) <= 64;
    }

    boolean canSee(@NotNull VirtualEntity entity, @NotNull Player player);

}
