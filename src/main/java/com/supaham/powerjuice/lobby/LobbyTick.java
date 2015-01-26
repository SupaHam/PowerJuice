package com.supaham.powerjuice.lobby;

import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class LobbyTick extends BukkitRunnable {
    
    private final LobbyManager lobbyManager;

    private int ticksToSecond = 0;
    private boolean secondTick = false;

    protected void start() {
        this.lobbyManager.getPlugin().getLog().finer("Starting LobbyTick...");
        runTaskTimer(lobbyManager.getPlugin(), 0, 1);
    }

    @Override
    public void run() {
        if (++ticksToSecond == 20) {
            secondTick = true;
            ticksToSecond = 0;
        }
        
        lobbyManager.getLobbyCountdown().tick(secondTick);
        
        this.secondTick = false;
    }
}
