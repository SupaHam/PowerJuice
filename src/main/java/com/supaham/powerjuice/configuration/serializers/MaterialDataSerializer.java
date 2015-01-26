package com.supaham.powerjuice.configuration.serializers;

import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pluginbase.config.serializers.Serializer;

/**
 * Created by Ali on 12/07/2014.
 */
public class MaterialDataSerializer implements Serializer<MaterialData> {

    @Nullable
    @Override
    public Object serialize(@Nullable MaterialData object) {
        return object.getItemType() + ":" + object.getData();
    }

    @Nullable
    @Override
    public MaterialData deserialize(@Nullable Object serialized, @NotNull Class wantedType)
            throws IllegalArgumentException {
        if (serialized == null) return null;
        String[] split = serialized.toString().split(":");
        return new MaterialData(org.bukkit.Material.matchMaterial(split[0]),
                                split.length >= 2 && !split[1].isEmpty() ? Byte.valueOf(split[1]) : 0);
    }
}
