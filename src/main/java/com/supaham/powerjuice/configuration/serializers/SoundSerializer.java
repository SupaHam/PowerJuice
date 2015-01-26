package com.supaham.powerjuice.configuration.serializers;

import com.supaham.powerjuice.misc.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pluginbase.config.serializers.Serializer;

public class SoundSerializer implements Serializer<Sound> {

    @Nullable
    @Override
    public Object serialize(@Nullable Sound object) {
        if(object == null) {
            return null;
        }
        return object.getSound() + " " + object.getVolume() + " " + object.getPitch();
    }

    @Nullable
    @Override
    public Sound deserialize(@Nullable Object serialized, @NotNull Class wantedType)
            throws IllegalArgumentException {
        if (serialized == null) {
            return Sound.NULL;
        }
        String[] split = serialized.toString().split(" ");
        float volume = !split[1].isEmpty() ? Float.valueOf(split[1]) : 0;
        float pitch = !split[2].isEmpty() ? Float.valueOf(split[2]) : 0;
        return new Sound(split[0], volume, pitch);
    }
}
