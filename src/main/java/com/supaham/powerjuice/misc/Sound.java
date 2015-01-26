package com.supaham.powerjuice.misc;

import java.util.List;

import lombok.Data;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a sound with a volume and pitch.
 */
@Data
public class Sound {

    public static final Sound NULL = new Sound("");
    private String sound;
    private float volume;
    private float pitch;

    /**
     * Constructs a Sound with a volume of 1 and pitch of 1.
     *
     * @param sound sound name to play
     */
    public Sound(String sound) {
        this(sound, 1f, 1f);
    }

    /**
     * Constructs a Sound.
     *
     * @param sound  sound name to play
     * @param volume volume to play the sound at
     * @param pitch  pitch to play the sound at
     */
    public Sound(@NotNull String sound, float volume, float pitch) {
        this.sound = sound;
        this.volume = volume;
        this.pitch = pitch;
    }

    /**
     * Plays this {@link Sound} in a world at the players' location.
     *
     * @param world world to play this sound in
     */
    public void play(@NotNull World world) {
        play(world.getPlayers());
    }

    /**
     * Plays this {@link Sound} in a world at a {@link Location}.
     *
     * @param world    world to play this sound in
     * @param location location to play this sound at
     */
    public void play(@NotNull World world, @NotNull Location location) {
        play(world.getPlayers(), location);
    }

    /**
     * Plays this {@link Sound} to a {@link List} of {@link Player}s at their location.
     *
     * @param players players to play this sound for
     */
    public void play(@NotNull List<Player> players) {
        players.forEach(this::play);
    }

    /**
     * Plays this {@link Sound} to a {@link List} of {@link Player}s.
     *
     * @param players  players to play this sound for
     * @param location location to play this sound at
     */
    public void play(@NotNull List<Player> players, @NotNull Location location) {
        for (Player player : players) {
            play(player, location);
        }
    }

    /**
     * Plays this {@link Sound} to a player at their location.
     *
     * @param player player to play this sound for
     */
    public void play(@NotNull Player player) {
        play(player, player.getLocation());
    }

    /**
     * Plays this {@link Sound} to a player.
     *
     * @param player   player to play this sound for
     * @param location location to play this sound at
     */
    @SuppressWarnings("deprecation")
    public void play(@NotNull Player player, @NotNull Location location) {
        player.playSound(location, this.sound, this.volume, this.pitch);
    }
}
