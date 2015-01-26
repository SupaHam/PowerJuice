package com.supaham.powerjuice.powerup;

import lombok.AccessLevel;
import lombok.Getter;
import pluginbase.config.SerializationRegistrar;
import pluginbase.config.annotation.Name;
import pluginbase.config.annotation.NoTypeKey;
import pluginbase.config.annotation.SerializableAs;
import pluginbase.config.properties.PropertiesWrapper;

@SerializableAs("PowerupProperties")
@NoTypeKey
@Getter(AccessLevel.PROTECTED)
public final class PowerupProperties extends PropertiesWrapper {

    static {
        SerializationRegistrar.registerClass(BoomShot.class);
        SerializationRegistrar.registerClass(IceShot.class);
        SerializationRegistrar.registerClass(RapidFire.class);
        SerializationRegistrar.registerClass(VolleyShot.class);
    }
    @Name("boom-shot")
    private BoomShot boomShot = new BoomShot();
    @Name("ice-shot")
    private IceShot iceShot = new IceShot();
    @Name("rapid-fire")
    private RapidFire rapidFire = new RapidFire();
    @Name("volley-shot")
    private VolleyShot volleyShot = new VolleyShot();
    private Speed speed = new Speed();

    @SerializableAs("BoomShot")
    @NoTypeKey
    @Getter(AccessLevel.PROTECTED)
    public static final class BoomShot {
        private int duration = 20;
        private int killRange = 5;
    }
    
    @SerializableAs("IceShot")
    @NoTypeKey
    @Getter(AccessLevel.PROTECTED)
    public static final class IceShot {
        private int duration = 20;
        private int shots = 10;
        private double accuracy = 0.4;
    }
    
    @SerializableAs("RapidFire")
    @NoTypeKey
    @Getter(AccessLevel.PROTECTED)
    public static final class RapidFire {
        private int duration = 20;
    }
    
    @SerializableAs("VolleyShot")
    @NoTypeKey
    @Getter(AccessLevel.PROTECTED)
    public static final class VolleyShot {
        private int duration = 20;
        private int shots = 6;
        private float speed = 3;
        private float spread = 2;
        private double accuracy = 0.4;
    }

    @NoTypeKey
    @Getter(AccessLevel.PROTECTED)
    public static final class Speed {
        private int duration = 20;
        private int speedLevel = 1;
        private boolean ambient = true;
    }
}
