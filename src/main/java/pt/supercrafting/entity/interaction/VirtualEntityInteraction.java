package pt.supercrafting.entity.interaction;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pt.supercrafting.entity.type.VirtualEntity;

@FunctionalInterface
public interface VirtualEntityInteraction {

    void onInteract(final @NotNull Player player,
                    final @NotNull VirtualEntity entity,
                    final WrapperPlayClientInteractEntity.@NotNull InteractAction action);
}
