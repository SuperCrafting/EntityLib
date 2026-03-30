package pt.supercrafting.entity.tick;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import pt.supercrafting.entity.util.ReferenceRegistry;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

record VirtualEntityTickingActionHolderImpl(
        ReferenceRegistry<VirtualEntityTickingAction> handle) implements VirtualEntityTickingActionHolder {

    VirtualEntityTickingActionHolderImpl() {
        this(new ReferenceRegistry<>());
    }

    @Override
    public void onTick(int currentTick) {
        for (VirtualEntityTickingAction action : this.handle.values())
            action.onTick(currentTick);
    }

    @Override
    public @UnmodifiableView Collection<@NotNull VirtualEntityTickingAction> actions() {
        return handle.values();
    }

    @Override
    public @NotNull UUID registerAction(@NotNull VirtualEntityTickingAction action) {
        Objects.requireNonNull(action, "action cannot be null");
        return handle.register(action);
    }

    @Override
    public @Nullable VirtualEntityTickingAction unregisterAction(@NotNull UUID actionId) {
        Objects.requireNonNull(actionId, "actionId cannot be null");
        return handle.unregister(actionId);
    }

}
