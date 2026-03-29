package pt.supercrafting.entity;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.google.common.collect.Lists;
import io.github.retrooper.packetevents.util.SpigotReflectionUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import pt.supercrafting.entity.interaction.VirtualEntityPacketListener;
import pt.supercrafting.entity.task.EntityTickingTask;
import pt.supercrafting.entity.type.VirtualEntity;
import pt.supercrafting.entity.type.VirtualHumanEntity;

import java.util.Collection;
import java.util.Optional;

public final class EntityLib {

    private final Plugin plugin;
    private final Collection<VirtualEntity> entities;

    public EntityLib(final Plugin plugin) {
        this.plugin = plugin;
        this.entities = Lists.newArrayList();

        PacketEvents.getAPI().getEventManager().registerListener(new VirtualEntityPacketListener(this));

        Bukkit.getScheduler().runTaskTimerAsynchronously(
                plugin,
                new EntityTickingTask(this),
                0L,
                1L);
    }

    public Collection<VirtualEntity> entities() {
        return this.entities;
    }

    public Optional<VirtualEntity> entityById(final int id) {
        return this.entities.stream()
                .filter(virtualEntity -> virtualEntity.id() == id)
                .findFirst();
    }

    public VirtualEntity createEntity(final Location location, final EntityType entityType) {
        if (entityType == EntityTypes.PLAYER) {
            return this.createHuman(location);
        }

        final var entity = VirtualEntity.create(
                SpigotReflectionUtil.generateEntityId(),
                entityType,
                location
        );
        this.entities.add(entity);
        return entity;
    }

    public VirtualHumanEntity createHuman(final Location location) {
        final var human = VirtualHumanEntity.create(
                SpigotReflectionUtil.generateEntityId(),
                location
        );
        this.entities.add(human);
        return human;
    }
}
