package com.supaham.powerjuice.weapon;

import java.util.HashMap;
import java.util.Map;

import com.supaham.powerjuice.PowerJuicePlugin;
import lombok.Getter;

/**
 * Represents a {@link Weapon} manager.
 */
public class WeaponManager {

    protected final PowerJuicePlugin plugin;

    @Getter
    private final Map<String, Weapon> weapons = new HashMap<>();

    public WeaponManager(PowerJuicePlugin plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        addWeapon(new SuperBow(this));
        addWeapon(new Arrow(this));
    }

    public Weapon getWeapon(String weaponName) {
        return this.weapons.get(weaponName.toLowerCase());
    }

    private void addWeapon(Weapon weapon) {
        plugin.getLog().fine("Adding weapon '" + weapon.getName() + "'.");
        this.weapons.put(weapon.getName(), weapon);
    }

    private void removeWeapon(Weapon weapon) {
        removeWeapon(weapon.getName());
    }

    private Weapon removeWeapon(String weaponName) {
        plugin.getLog().fine("Removing weapon '" + weaponName + "'.");
        return this.weapons.remove(weaponName);
    }

    @Override
    public String toString() {
        return toString(this.plugin.getLog().getDebugLevel() < 2);
    }

    public String toString(boolean simple) {
        int debugLevel = plugin.getLog().getDebugLevel();
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append("{")
                .append("total-weapons=").append(weapons.size());
        if (!simple && debugLevel > 1) {
            sb.append(",weapons=").append(weapons);
        }
        sb.append("}");
        return sb.toString();
    }
}
