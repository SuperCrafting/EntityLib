package pt.supercrafting.entity.interaction;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientInteractEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import pt.supercrafting.entity.type.VirtualEntity;
import pt.supercrafting.entity.util.ReferenceRegistry;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

record VirtualEntityInteractionHolderImpl(ReferenceRegistry<VirtualEntityInteraction> handle) implements VirtualEntityInteractionHolder {

    VirtualEntityInteractionHolderImpl() {
        this(new ReferenceRegistry<>());
    }

    @Override
    public void onInteract(@NotNull Player player, @NotNull VirtualEntity entity, WrapperPlayClientInteractEntity.@NotNull InteractAction action) {
        Objects.requireNonNull(player, "player cannot be null");
        Objects.requireNonNull(entity, "entity cannot be null");
        Objects.requireNonNull(action, "action cannot be null");
        for (VirtualEntityInteraction interaction : this.handle.values())
            interaction.onInteract(player, entity, action);
    }

    @UnmodifiableView
    @Override
    public Collection<@NotNull VirtualEntityInteraction> interactions() {
        return handle.values();
    }

    @Override
    public @NotNull UUID registerInteraction(@NotNull VirtualEntityInteraction interaction) {
        Objects.requireNonNull(interaction, "interaction cannot be null");
        return handle.register(interaction);
    }

    @Override
    public @Nullable VirtualEntityInteraction unregisterInteraction(@NotNull UUID interactionId) {
        Objects.requireNonNull(interactionId, "interactionId cannot be null");
        return handle.unregister(interactionId);
    }

}
