package pt.supercrafting.entity;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.plugin.java.JavaPlugin;
import pt.supercrafting.entity.command.EntityLibCommand;

public final class EntityLibPlugin extends JavaPlugin {

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        PacketEvents.getAPI().init();

        final var lib = new EntityLib(this);

        final var executor = new EntityLibCommand(lib, this);
        this.getCommand("entitylib").setExecutor(executor);
        this.getCommand("entitylib").setTabCompleter(executor);
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
    }
}
