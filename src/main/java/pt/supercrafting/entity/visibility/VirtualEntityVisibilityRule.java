package pt.supercrafting.entity.visibility;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pt.supercrafting.entity.type.VirtualEntity;

public interface VirtualEntityVisibilityRule {

    @NotNull
    static VirtualEntityVisibilityRule always() {
        return (entity, player) -> true;
    }

    boolean canSee(@NotNull VirtualEntity entity, @NotNull Player player);

}
