package pt.supercrafting.entity.visibility;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pt.supercrafting.entity.type.VirtualEntity;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

record VirtualEntityVisibilityImpl(@NotNull VirtualEntity entity, @NotNull Map<UUID, VirtualEntityVisibilityRule> rules, @NotNull Collection<Player> viewers) implements VirtualEntityVisibility {

    public VirtualEntityVisibilityImpl(@NotNull VirtualEntity entity) {
        this(entity, new ConcurrentHashMap<>(), ConcurrentHashMap.newKeySet(4));
    }

    public VirtualEntityVisibilityImpl(@NotNull VirtualEntity entity, @NotNull Map<UUID, VirtualEntityVisibilityRule> rules, @NotNull Collection<Player> viewers) {
        this.entity = Objects.requireNonNull(entity, "entity cannot be null");
        this.rules = Objects.requireNonNull(rules, "rules cannot be null");
        this.viewers = Objects.requireNonNull(viewers, "viewers cannot be null");
    }

    @Override
    public void run() {

        if(!this.entity.isValid())
            return;


    }

    @Override
    public boolean isViewer(@NotNull Player player) {
        return viewers.contains(player);
    }

    @Override
    public boolean addViewer(@NotNull Player player) {
        return viewers.add(player);
    }

    @Override
    public boolean removeViewer(@NotNull Player player) {
        return viewers.remove(player);
    }

    @Override
    public @NotNull UUID addRule(@NotNull VirtualEntityVisibilityRule rule) {
        Objects.requireNonNull(rule, "rule cannot be null");
        UUID id = UUID.randomUUID();
        rules.put(id, rule);
        return id;
    }

    @Override
    public @Nullable VirtualEntityVisibilityRule removeRule(@NotNull UUID ruleId) {
        Objects.requireNonNull(ruleId, "ruleId cannot be null");
        return rules.remove(ruleId);
    }

    @Override
    public boolean canSee(@NotNull VirtualEntity entity, @NotNull Player player) {
        for (VirtualEntityVisibilityRule rule : this.rules.values())
            if(!rule.canSee(entity, player))
                return false;
        return true;
    }

}
