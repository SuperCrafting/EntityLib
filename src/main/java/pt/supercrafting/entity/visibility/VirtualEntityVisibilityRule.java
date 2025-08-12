package pt.supercrafting.entity.visibility;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pt.supercrafting.entity.type.VirtualEntity;

public interface VirtualEntityVisibilityRule {

    boolean canSee(@NotNull VirtualEntity entity, @NotNull Player player);

}
