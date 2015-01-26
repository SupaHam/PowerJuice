package com.supaham.powerjuice.configuration.serializers;

import org.apache.commons.lang.math.IntRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pluginbase.config.serializers.Serializer;

public class IntRangeSerializer implements Serializer<IntRange> {

    @Override
    @Nullable
    public Object serialize(@Nullable final IntRange range) {
        if (range == null) {
            return null;
        }
        return range.getMinimumInteger() + (range.getMaximumInteger() != range.getMinimumInteger() ?
                                            " " + range.getMaximumInteger() : "");
    }

    @Override
    @Nullable
    public IntRange deserialize(@Nullable Object serialized, @NotNull Class wantedType)
            throws IllegalArgumentException {
        if (serialized == null || !(serialized instanceof String)) {
            return null;
        }
        String[] split = serialized.toString().split(" ");
        return new IntRange(Integer.parseInt(split[0]), Integer.parseInt(split[split.length > 1 ? 1 : 0]));
    }
}
