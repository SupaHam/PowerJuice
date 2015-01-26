package com.supaham.powerjuice.util;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a {@link List} that has {@link WeakReference}s of {@link Player}s.
 */
public class WeakList<T> implements Iterable<T> {

    private WeakHashMap<T, Void> map = new WeakHashMap<>();

    public void add(@NotNull T t) {
        map.put(t, null);
    }

    public boolean contains(@NotNull T t) {
        return map.containsKey(t);
    }
    
    public boolean remove(@NotNull T t) {
        return this.map.remove(t) != null;
    }
    
    public int size() {
        return this.map.size();
    }

    @Override
    public Iterator<T> iterator() {
        return map.keySet().iterator();
    }
}
