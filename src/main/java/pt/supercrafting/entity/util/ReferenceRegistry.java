package pt.supercrafting.entity.util;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ApiStatus.Internal
public final class ReferenceRegistry<V> implements Iterable<Map.Entry<UUID, V>> {

    private final Map<UUID, V> handle = new ConcurrentHashMap<>();
    private Set<UUID> keysView;
    private Collection<V> valuesView;

    public ReferenceRegistry() {
    }

    public UUID register(@NotNull V value) {
        Objects.requireNonNull(value, "Value cannot be null");
        UUID id = UUID.randomUUID();
        handle.put(id, value);
        return id;
    }

    @Nullable
    public V unregister(@NotNull UUID id) {
        return handle.remove(Objects.requireNonNull(id, "ID cannot be null"));
    }

    public boolean isEmpty() {
        return handle.isEmpty();
    }

    public int size() {
        return handle.size();
    }

    @UnmodifiableView
    @NotNull
    public Set<@NotNull UUID> keys() {
        if (keysView == null)
            keysView = Collections.unmodifiableSet(handle.keySet());
        return keysView;
    }

    @UnmodifiableView
    @NotNull
    public Collection<@NotNull V> values() {
        if (valuesView == null)
            valuesView = Collections.unmodifiableCollection(handle.values());
        return valuesView;
    }

    @Override
    public @NotNull Iterator<Map.Entry<@NotNull UUID, @NotNull V>> iterator() {
        return Collections.unmodifiableSet(handle.entrySet()).iterator();
    }

}
