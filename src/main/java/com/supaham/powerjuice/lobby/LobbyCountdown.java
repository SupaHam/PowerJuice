package com.supaham.powerjuice.lobby;

import com.google.common.base.Preconditions;
import com.supaham.powerjuice.misc.Sound;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;

@RequiredArgsConstructor
public class LobbyCountdown {

    private final LobbyManager lobbyManager;
    private int seconds = 0;
    @Getter
    private boolean started = false;

    public static final Sound HIGH_SOUND = new Sound("note.harp", 2, 1.3F);
    public static final Sound LOW_SOUND = new Sound("note.harp", 2, 0.8F);

    public void start() {
        if (this.started) {
            return;
        }
        
        if (this.lobbyManager.plugin.getArenaManager().getPlayableArenas().size() == 0) {
            return;
        }
        
        this.lobbyManager.getPlugin().getLog().fine("Starting LobbyCountdown...");
        
        if (this.seconds == 0) {
            this.seconds = lobbyManager.getLobbyProperties().getCountdown();
            if (this.seconds == 0) {
                this.seconds = 5;
            }
        }
        this.lobbyManager.getPlugin().getLog().fine(this.seconds + " before the game starts...");
        this.started = true;
    }

    public int pause() {
        this.started = false;
        return this.seconds;
    }

    public int stop() {
        if (!this.started) {
            return -1;
        }
        this.started = false;
        int old = this.seconds;
        this.seconds = 0;
        return old;
    }

    public void setCountdown(int seconds) throws IllegalArgumentException {
        Preconditions.checkArgument(seconds > 0, "seconds cannot be 0 or smaller.");
        this.lobbyManager.getPlugin().getLog().fine("LobbyCountdown seconds set to " + this.seconds + ".");
        this.seconds = seconds;
    }

    public void tick(boolean secondTick) {
        if (!this.started || !secondTick) {
            return;
        }
        this.lobbyManager.getPlugin().getLog().finer("Ticking LobbyCountdown... " + this.seconds + " seconds to go.");
        for (LobbySession session : this.lobbyManager.getSessions().values()) {
            session.getBukkitPlayer().setLevel(this.seconds);
            switch (this.seconds) {
                case 60:
                case 30:
                case 10:
                case 5:
                case 4:
                case 3:
                case 2:
                case 1:
                    notify(session);
            }
            if (this.seconds <= 5 && this.seconds > 0) {
                if (this.seconds == 1) {
                    HIGH_SOUND.play(session.getBukkitPlayer());
                } else {
                    LOW_SOUND.play(session.getBukkitPlayer());
                }
            }
        }
        if (this.seconds == 0) {
            this.started = false;
            this.lobbyManager.getPlugin().getGameManager().start();
            return;
        }
        this.seconds--;
    }
    
    private void notify(LobbySession session) {
        session.getBukkitPlayer().sendMessage(ChatColor.BLUE + "" + this.seconds +
                                              ChatColor.YELLOW + " before the game starts.");
    }
}
