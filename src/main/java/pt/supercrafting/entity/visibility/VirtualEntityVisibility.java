package pt.supercrafting.entity.visibility;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import pt.supercrafting.entity.type.VirtualEntity;

import java.util.Collection;
import java.util.UUID;

public sealed interface VirtualEntityVisibility extends VirtualEntityVisibilityRule, VirtualEntityVisibilityListener, Runnable permits VirtualEntityVisibilityImpl {

    static VirtualEntityVisibility create(@NotNull VirtualEntity entity) {
        return new VirtualEntityVisibilityImpl(entity);
    }

    boolean isViewer(@NotNull Player player);
    boolean addViewer(@NotNull Player player);
    boolean removeViewer(@NotNull Player player);

    @Unmodifiable
    Collection<Player> viewers();

    @NotNull
    UUID addRule(@NotNull VirtualEntityVisibilityRule rule);

    @Nullable
    VirtualEntityVisibilityRule removeRule(@NotNull UUID ruleId);

    @NotNull
    UUID addListener(@NotNull VirtualEntityVisibilityListener listener);

    @Nullable
    VirtualEntityVisibilityListener removeListener(@NotNull UUID listenerId);

}
