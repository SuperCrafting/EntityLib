package pt.supercrafting.entity.interaction;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.UUID;

public sealed interface VirtualEntityInteractionHolder extends VirtualEntityInteraction permits VirtualEntityInteractionHolderImpl {

    @ApiStatus.Internal
    @NotNull
    static VirtualEntityInteractionHolder create() {
        return new VirtualEntityInteractionHolderImpl();
    }

    @UnmodifiableView
    Collection<@NotNull VirtualEntityInteraction> interactions();

    @NotNull
    UUID registerInteraction(final @NotNull VirtualEntityInteraction interaction);

    @Nullable
    VirtualEntityInteraction unregisterInteraction(final @NotNull UUID interactionId);

}
