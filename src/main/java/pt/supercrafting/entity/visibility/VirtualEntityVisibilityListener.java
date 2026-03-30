package pt.supercrafting.entity.visibility;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pt.supercrafting.entity.type.VirtualEntity;

public interface VirtualEntityVisibilityListener {

    default void onShow(@NotNull VirtualEntity entity, @NotNull Player player) {

    }

    default void onHide(@NotNull VirtualEntity entity, @NotNull Player player) {

    }

}
