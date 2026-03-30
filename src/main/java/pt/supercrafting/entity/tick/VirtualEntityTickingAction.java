package pt.supercrafting.entity.tick;

@FunctionalInterface
public interface VirtualEntityTickingAction {

    void onTick(final int currentTick);

}
