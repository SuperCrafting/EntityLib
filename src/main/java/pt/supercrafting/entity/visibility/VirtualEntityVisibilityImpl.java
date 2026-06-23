package pt.supercrafting.entity.visibility;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pt.supercrafting.entity.type.VirtualEntity;
import pt.supercrafting.entity.util.ReferenceRegistry;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

record VirtualEntityVisibilityImpl(@NotNull VirtualEntity entity,
                                   @NotNull ReferenceRegistry<VirtualEntityVisibilityRule> rules,
                                   @Nullable UUID fallBackRule,
                                   @NotNull ReferenceRegistry<VirtualEntityVisibilityListener> listeners,
                                   @NotNull Collection<Player> viewers) implements VirtualEntityVisibility {

    public VirtualEntityVisibilityImpl(@NotNull VirtualEntity entity) {
        this(entity, new ReferenceRegistry<>(), new ReferenceRegistry<>(), ConcurrentHashMap.newKeySet(4));
    }

    public VirtualEntityVisibilityImpl(@NotNull VirtualEntity entity, @NotNull ReferenceRegistry<VirtualEntityVisibilityRule> rules, @NotNull ReferenceRegistry<VirtualEntityVisibilityListener> listeners, @NotNull Collection<Player> viewers) {
        this(
                Objects.requireNonNull(entity, "entity cannot be null"),
                Objects.requireNonNull(rules, "rules cannot be null"),
                UUID.randomUUID(),
                Objects.requireNonNull(listeners, "listeners cannot be null"),
                Objects.requireNonNull(viewers, "viewers cannot be null"));
    }

    VirtualEntityVisibilityImpl {
        rules.register(VirtualEntityVisibilityRule.always(), fallBackRule);
    }

    @Override
    public void run() {
        if (!this.entity.isValid()) {
            return;
        }

        for (final var player : Bukkit.getOnlinePlayers()) {
            if (!this.canSee(this.entity, player) && this.isViewer(player)) {
                this.removeViewer(player);
            } else if (this.canSee(this.entity, player) && !this.isViewer(player)) {
                this.addViewer(player);
            }
        }
    }

    @Override
    public boolean isViewer(@NotNull Player player) {
        return viewers.contains(player);
    }

    @Override
    public boolean addViewer(@NotNull Player player) {
        Objects.requireNonNull(player, "player cannot be null");
        if (viewers.add(player)) {
            this.entity.update(entity.spawn(), Collections.singleton(player));
            this.entity.visibility().onShow(entity, player);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeViewer(@NotNull Player player) {
        Objects.requireNonNull(player, "player cannot be null");
        if (viewers.remove(player)) {
            this.entity.update(entity.destroy(), Collections.singleton(player));
            this.entity.visibility().onHide(entity, player);
            return true;
        }
        return false;
    }

    @Override
    public @NotNull UUID addRule(@NotNull VirtualEntityVisibilityRule rule) {
        Objects.requireNonNull(rule, "rule cannot be null");
        return rules.register(rule);
    }

    @Override
    public @Nullable VirtualEntityVisibilityRule removeRule(@NotNull UUID ruleId) {
        Objects.requireNonNull(ruleId, "ruleId cannot be null");
        return rules.unregister(ruleId);
    }

    @Override
    public boolean canSee(@NotNull VirtualEntity entity, @NotNull Player player) {
        Objects.requireNonNull(entity, "entity cannot be null");
        Objects.requireNonNull(player, "player cannot be null");
        for (VirtualEntityVisibilityRule rule : this.rules.values())
            if (!rule.canSee(entity, player))
                return false;
        return true;
    }

    @Override
    public @NotNull UUID addListener(@NotNull VirtualEntityVisibilityListener listener) {
        Objects.requireNonNull(listener, "listener cannot be null");
        return listeners.register(listener);
    }

    @Override
    public @Nullable VirtualEntityVisibilityListener removeListener(@NotNull UUID listenerId) {
        Objects.requireNonNull(listenerId, "listenerId cannot be null");
        return listeners.unregister(listenerId);
    }

    @Override
    public void onShow(@NotNull VirtualEntity entity, @NotNull Player player) {
        for (VirtualEntityVisibilityListener listener : this.listeners.values())
            listener.onShow(entity, player);
    }

    @Override
    public void onHide(@NotNull VirtualEntity entity, @NotNull Player player) {
        for (VirtualEntityVisibilityListener listener : this.listeners.values())
            listener.onHide(entity, player);
    }

}
