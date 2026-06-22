package pt.supercrafting.entity.tick;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.UUID;

public sealed interface VirtualEntityTickingActionHolder extends VirtualEntityTickingAction permits VirtualEntityTickingActionHolderImpl {

    @ApiStatus.Internal
    @NotNull
    static VirtualEntityTickingActionHolder create() {
        return new VirtualEntityTickingActionHolderImpl();
    }

    @UnmodifiableView
    Collection<@NotNull VirtualEntityTickingAction> actions();

    @NotNull
    UUID registerAction(final @NotNull VirtualEntityTickingAction action);

    @Nullable
    VirtualEntityTickingAction unregisterAction(final @NotNull UUID actionId);

}
