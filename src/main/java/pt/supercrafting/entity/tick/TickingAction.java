package pt.supercrafting.entity.tick;

@FunctionalInterface
public interface TickingAction {
    
    void onTick(final int currentTick);
}
