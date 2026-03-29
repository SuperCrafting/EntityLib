package pt.supercrafting.entity.interaction;

import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import org.jetbrains.annotations.NotNull;
import pt.supercrafting.entity.type.VirtualEntity;

@FunctionalInterface
public interface VirtualEntityInteraction {

    void onInteract(final @NotNull User user,
                    final WrapperPlayClientInteractEntity.@NotNull InteractAction action,
                    final @NotNull VirtualEntity entity);
}
