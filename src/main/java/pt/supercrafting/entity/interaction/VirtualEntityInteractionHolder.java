package pt.supercrafting.entity.interaction;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;

public interface VirtualEntityInteractionHolder {

    @UnmodifiableView
    Collection<@NotNull VirtualEntityInteraction> interactions();

    void registerInteraction(final @NotNull VirtualEntityInteraction interaction);

    void unregisterInteraction(final @NotNull VirtualEntityInteraction interaction);
}
