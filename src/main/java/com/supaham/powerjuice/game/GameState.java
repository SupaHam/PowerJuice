package com.supaham.powerjuice.game;

/**
 * Represents a game status that a {@link GameManager} can have.
 */
public enum GameState {
    /**
     * This status is used when the {@link GameManager} doesn't have enough players that are ready to play.
     */
    WAITING_FOR_PLAYERS,
    /**
     * This status is used when the {@link GameManager} is starting a game.
     */
    STARTING,
    /**
     * This status is used when a {@link Game} has already begun.
     */
    STARTED,
    /**
     * This status is used when a {@link Game} has ended.
     */
    ENDED,
    /**
     * This status is used when a {@link GameManager} is forced to idle.
     */
    IDLE
}
