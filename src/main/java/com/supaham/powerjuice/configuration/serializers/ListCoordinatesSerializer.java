package com.supaham.powerjuice.configuration.serializers;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pluginbase.config.serializers.Serializer;
import pluginbase.minecraft.location.Coordinates;

public class ListCoordinatesSerializer implements Serializer<List<Coordinates>> {
    
    @Nullable
    @Override
    public Object serialize(@Nullable List<Coordinates> object) {
        if (object == null) return null;
        CoordinatesSerializer ser = new CoordinatesSerializer();
        return object.stream().map(ser::serialize).collect(Collectors.toCollection(LinkedList::new));
    }

    @Nullable
    @Override
    public List<Coordinates> deserialize(@Nullable Object serialized, @NotNull Class wantedType)
            throws IllegalArgumentException {
        if (serialized == null || !(serialized instanceof List)) return null;
        CoordinatesSerializer ser = new CoordinatesSerializer();
        return ((List<String>) serialized).stream()
                .map(coordinates -> ser.deserialize(coordinates, Coordinates.class))
                .collect(Collectors.toList());
    }
}
