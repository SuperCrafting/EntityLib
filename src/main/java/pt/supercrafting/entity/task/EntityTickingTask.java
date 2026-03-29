package pt.supercrafting.entity.task;

import pt.supercrafting.entity.EntityLib;

public final class EntityTickingTask implements Runnable {

    private final EntityLib entityLib;
    private int currentTick;

    public EntityTickingTask(final EntityLib entityLib) {
        this.entityLib = entityLib;
    }

    @Override
    public void run() {
        for (final var entity : this.entityLib.entities()) {
            for (final var action : entity.tickingActions()) {
                action.onTick(this.currentTick);
            }
            entity.visibility().run();
        }

        this.currentTick++;
    }
}
