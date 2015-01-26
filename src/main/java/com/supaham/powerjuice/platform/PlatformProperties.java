package com.supaham.powerjuice.platform;

import com.supaham.powerjuice.configuration.serializers.MaterialDataSerializer;
import com.supaham.powerjuice.configuration.serializers.SoundSerializer;
import com.supaham.powerjuice.misc.Sound;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.NotNull;
import pluginbase.config.annotation.Comment;
import pluginbase.config.annotation.Immutable;
import pluginbase.config.annotation.Name;
import pluginbase.config.annotation.SerializableAs;
import pluginbase.config.annotation.SerializeWith;
import pluginbase.config.properties.PropertiesWrapper;

@Getter
@Setter
@SerializableAs("PlatformProperties")
public class PlatformProperties extends PropertiesWrapper {

    @Comment("The name of this Platform. Changing this could break the plugin.")
    @Immutable
    @NotNull
    @Setter(AccessLevel.PRIVATE)
    private String name;

    @Comment({"This is the \"Human\" name that is used as an alias for this platform.",
              "This is the na,e that is displayed to the players in the game"})
    private String displayName = "";

    @Name("material")
    @SerializeWith(MaterialDataSerializer.class)
    private MaterialData materialData = new MaterialData(Material.STAINED_GLASS, (byte) 1);

    @Comment("This is the sound that is played when the storm starts.")
    @SerializeWith(SoundSerializer.class)
    private Sound sound = Sound.NULL;

    protected PlatformProperties() {
    }

    public PlatformProperties(@NotNull String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() 
               + "{"
               + "name=" + this.name
               + ",displayName=" + this.displayName
               + ",materialData=" + this.materialData
               + ",sound=" + sound
               + "}";
    }
}
