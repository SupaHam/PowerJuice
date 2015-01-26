package com.supaham.powerjuice.worldedit.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that this value should come from the current selection.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Selection {
    
    public Class<? extends com.sk89q.worldedit.bukkit.selections.Selection>[] value() default {};

}
