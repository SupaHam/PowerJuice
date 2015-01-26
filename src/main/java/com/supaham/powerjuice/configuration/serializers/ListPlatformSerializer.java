package com.supaham.powerjuice.configuration.serializers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.supaham.powerjuice.platform.PlatformProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pluginbase.config.serializers.Serializer;

public class ListPlatformSerializer implements Serializer<Map<String, PlatformProperties>> {
    
    @Nullable
    @Override
    public Object serialize(@Nullable Map<String, PlatformProperties> object) {
        if (object == null) return null;
        return object.values().stream().collect(Collectors.toCollection(LinkedList::new));
    }

    @Nullable
    @Override
    public Map<String, PlatformProperties> deserialize(@Nullable Object serialized, @NotNull Class wantedType)
            throws IllegalArgumentException {
        if (serialized == null || !(serialized instanceof List)) return null;
        Map<String, PlatformProperties> res = new HashMap<>();
        for (PlatformProperties props : (List<PlatformProperties>) serialized) {
            res.put(props.getName(), props);
        }
        return res;
    }
}
