package pt.supercrafting.entity.type;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

public sealed interface VirtualBukkitEntity<E extends Entity> extends VirtualEntity permits VirtualBukkitEntityImpl {

    void modify(@NotNull Consumer<@NotNull E> consumer);

    <T> T access(@NotNull Function<@NotNull E, T> function);

}
