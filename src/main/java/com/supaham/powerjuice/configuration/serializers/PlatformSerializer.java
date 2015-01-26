package com.supaham.powerjuice.configuration.serializers;

import com.supaham.powerjuice.platform.PlatformProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pluginbase.config.serializers.Serializer;
import pluginbase.config.serializers.Serializers;

public class PlatformSerializer implements Serializer<PlatformProperties> {

    @Nullable
    @Override
    public Object serialize(@Nullable PlatformProperties platform) {
        return platform == null ? null : Serializers.defaultSerialize(platform);
    }

    @Nullable
    @Override
    public PlatformProperties deserialize(@Nullable Object serialized, @NotNull Class wantedType)
            throws IllegalArgumentException {
        return serialized == null ? null : Serializers.defaultDeserialize(serialized, PlatformProperties.class);
    }
}
