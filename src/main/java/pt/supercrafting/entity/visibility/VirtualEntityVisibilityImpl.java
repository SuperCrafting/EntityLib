package pt.supercrafting.entity.visibility;

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityAnimation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pt.supercrafting.entity.type.VirtualEntity;
import pt.supercrafting.entity.update.VirtualEntityUpdate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

record VirtualEntityVisibilityImpl(@NotNull VirtualEntity entity, @NotNull Map<UUID, VirtualEntityVisibilityRule> rules,
                                   @NotNull Collection<Player> viewers) implements VirtualEntityVisibility {

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
        if (!this.entity.isValid()) {
            return;
        }

        for (final var player : this.viewers) {
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
        this.entity.update(update -> update.packetFactory().spawn(), Collections.singleton(player));
        this.entity.onSpawn(player);
        return viewers.add(player);
    }

    @Override
    public boolean removeViewer(@NotNull Player player) {
        this.entity.update(update -> update.packetFactory().destroy(), Collections.singleton(player));
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
            if (!rule.canSee(entity, player))
                return false;
        return true;
    }

}
