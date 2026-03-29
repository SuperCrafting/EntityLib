package pt.supercrafting.entity.tick;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;

public interface TickingActionHolder {

    @UnmodifiableView
    Collection<@NotNull TickingAction> tickingActions();

    void registerTickingAction(final @NotNull TickingAction action);

    void unregisterTickingAction(final @NotNull TickingAction action);
}
