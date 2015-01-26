package com.supaham.powerjuice.util;

import com.supaham.powerjuice.language.Message;
import com.supaham.powerjuice.language.MessageManager;
import com.supaham.powerjuice.language.Theme;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

public final class Language {

    @Getter
    private static MessageManager manager;

    static {
        manager = new MessageManager();
        manager.addTheme(new Theme('+', ChatColor.YELLOW.toString()));
        manager.addTheme(new Theme('-', ChatColor.RED.toString()));
        manager.addTheme(new Theme('v', ChatColor.BLUE.toString()));
        manager.addTheme(new Theme('!', ChatColor.BOLD.toString()));
        manager.addTheme(new Theme('_', ChatColor.UNDERLINE.toString()));
    }

    private static Message m(@NotNull String node, @NotNull String message) {
        Message m = new Message(manager, node, message);
        manager.addMessage(m);
        return m;
    }

    public static final Message SKILL_NOT_FOUND = m("skill_not_found", "$-'%s' is not a valid skill.");

    public static final Message MAKE_WE_SEL = m("make_we_sel", "$-Please make a WorldEdit selection first.");

    public static final Message ONLY_CUBOID_SUPP = m("only_cuboid_supp", "$-Only cuboid selections are supported. :(");

    public static final Message TM_DIRECTION_NOT_FOUND =
            m("direction.not_found", "$-'$v%s$-' is not a valid Direction.");

    public static final Message UNKNOWN_MATERIAL = m("unknown_material", "$-Unknown material '$v%s%-'.");

    public static final Message PLAYER_NOT_ONLINE = m("player_not_online", "$-'$v%s%-' is not online.");
    
    public static final Message NO_GAMER_SESSION = m("no_gamer_session", "$-'$v%s%-' doesn't have a Gamer session.");

    public static final Message OOB_WARN = m("out_of_bounds.warn", 
                                             "$-You are out of bounds! You've got $v%s $-seconds before you die.");
    
    public static final Message OOB_BACK = m("out_of_bounds.back", "$+You are back in bounds.");

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Arena {

        public static final Message ARENA_INFO_SETUP = m("arena.info_setup", "$+%s$+: %s");
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Game {

        public static final Message TIME_INCREASE_SUCCESS = m("game.time.increase", "$+Game time increased by $v%s.");

        public static final Message TIME_REDUCE_SUCCESS = 
                m("game.time.reduce.success",
                  "$+Game time reduced by $v%s$+ seconds.");

        public static final Message TIME_SET_SUCCESS = 
                m("game.time.set.success",
                  "$+Game now has $v%s$+ seconds remaining.");

        public static final Message POWERUP_ALREADY_ACTIVE =
                m("game.powerup.already_active", "$-%s$- is already active.");

        public static final Message GO_TO_PLATFORM =
                m("game.go_to_platform", "$+Ground yourself on the %s$+ platforms.");
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Command {

        public static final Message ANTI_PLAYER_CMD = m("anti_player_cmd", "noob, you'll never be human!!!!");

        public static final Message ILLEGAL_CHARS =
                m("cmd.arena.illegal_chars", "$-'$v%s$-' contains illegal characters.");

        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static final class Lobby {

            public static final Message NOT_CREATED =
                    m("cmd.lobby.not_created", "$-The Lobby hasn't been created yet.");

            public static final Message CREATE_SUCCESS =
                    m("cmd.arena.create.success",
                      "$+You've successfully created the Lobby.");
            
            public static final Message REDEFINE_SUCCESS =
                    m("cmd.lobby.redefine.success",
                      "$+You've successfully redefined the Lobby's $v$!$_boundaries$+.");

            public static final Message SET_SUCCESS =
                    m("cmd.lobby.set.success",
                      "$+You've successfully set the Lobby's $v$!$_%s$+ to '$v%s$+'.");

            public static final Message ADD_SPAWN_SUCCESS =
                    m("cmd.lobby.add.spawn.success",
                      "$+You've successfully added a spawnpoint to the Lobby.");

            public static final Message ADD_SPAWN_SUCCESS_OVERWRITE =
                    m("cmd.lobby.add.spawn.success_overwrite",
                      "$+You've successfully overwrote a spawnpoint in the Lobby.");

            public static final Message REMOVE_SPAWN_SUCCESS =
                    m("cmd.lobby.remove.spawn.success",
                      "$+You've successfully removed a spawnpoint from the Lobby.");

            public static final Message REMOVE_SPAWN_FAILED =
                    m("cmd.lobby.remove.spawn.failed",
                      "$-The Lobby doesn't have a spawnpoint at your location.");
        }

        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static final class Arena {

            public static final Message NOT_FOUND =
                    m("cmd.arena.not_found", "$-Could not find an Arena by the name of '$v%s$-'.");

            public static final Message ALREADY_EXISTS =
                    m("cmd.arena.already_exists", "$-An Arena by the name '$v%s$-' already exists.");

            public static final Message CREATE_SUCCESS =
                    m("cmd.arena.create.success",
                      "$+You've successfully created an Arena called '$v%s$+'.");
            
            public static final Message REDEFINE_SUCCESS =
                    m("cmd.arena.redefine.success",
                      "$+You've successfully redefined Arena $v$!%s$+'s $v$!$_boundaries$+.");

            public static final Message NO_SUCH_PROPERTY =
                    m("cmd.arena.set.no_such_property", "$-There is no such property as '$v$!$_%s$-'.");

            public static final Message SET_SUCCESS =
                    m("cmd.arena.set.success",
                      "$+You've successfully set Arena $v$!%s$+'s $v$!$_%s$+ to '$v%s$+'.");

            public static final Message SET_FAILED = m("cmd.arena.set.failed", "$-'$v$!$_%s$-' cannot be set.");

            public static final Message ADD_SUCCESS =
                    m("cmd.arena.add.success",
                      "$+You've successfully added '$v%s$+' to Arena $v$!%s$+'s $v$!$_%s$+.");

            public static final Message ADD_SPAWN_SUCCESS =
                    m("cmd.arena.add.spawn.success",
                      "$+You've successfully added a spawnpoint to Arena $v$!%s$+.");

            public static final Message ADD_SPAWN_SUCCESS_OVERWRITE =
                    m("cmd.arena.add.spawn.success_overwrite",
                      "$+You've successfully overwrote a spawnpoint in Arena $v$!%s$+.");

            public static final Message ADD_POWERUP_SUCCESS =
                    m("cmd.arena.add.powerup.success",
                      "$+You've successfully added a powerup location to Arena $v$!%s$+.");

            public static final Message ADD_POWERUP_SUCCESS_OVERWRITE =
                    m("cmd.arena.add.powerup.success_overwrite",
                      "$+You've successfully overwrote a powerup location in Arena $v$!%s$+.");

            public static final Message ADD_FAILED = m("cmd.arena.add.failed", "$-'$v$!$_%s$-' cannot be added to.");

            public static final Message REMOVE_SUCCESS =
                    m("cmd.arena.remove.success",
                      "$+You've successfully removed '$v%s$+' from Arena $v$!%s$+'s $v$!$_%s$+.");

            public static final Message REMOVE_FAILED =
                    m("cmd.arena.remove.failed", "$-'$v$!$_%s$-' cannot be removed from.");

            public static final Message REMOVE_SPAWN_SUCCESS =
                    m("cmd.arena.remove.spawn.success",
                      "$+You've successfully removed a spawnpoint from Arena $v$!%s$+.");

            public static final Message REMOVE_SPAWN_FAILED =
                    m("cmd.arena.remove.spawn.failed",
                      "$-Arena $v$!%s$- doesn't have a spawnpoint at your location.");

            public static final Message REMOVE_POWERUP_SUCCESS =
                    m("cmd.arena.remove.powerup.success",
                      "$+You've successfully removed a powerup from Arena $v$!%s$+.");

            public static final Message REMOVE_POWERUP_FAILED =
                    m("cmd.arena.remove.powerup.failed",
                      "$-Arena $v$!%s$- doesn't have a powerup at your location.");

            @NoArgsConstructor(access = AccessLevel.PRIVATE)
            public static final class Platform {
                public static final Message NOT_FOUND =
                        m("cmd.arena.platform.not_found",
                          "$+No Platform called '$v%s$+' could be found in Arena $v%s$+.");
                
                public static final Message CREATE_SUCCESS =
                        m("cmd.arena.platform.create.success",
                          "$+You've successfully created a Platform called $v%s$+ in Arena $v%s$+.");
                public static final Message DELETE_SUCCESS =
                        m("cmd.arena.platform.delete.success",
                          "$+You've successfully deleted Platform $v%s$+ from Arena $v%s$+.");

                public static final Message SET_SOUND_SUCCESS =
                        m("cmd.arena.platform.set.sound.success",
                          "$+You've successfully set Platform $v%s$+'s sound to '$v%s$+'.");
                public static final Message SET_DISPLAY_NAME_SUCCESS =
                        m("cmd.arena.platform.set.display_name.success",
                          "$+You've successfully set Platform $v%s$+'s display name to '$v%s$+'.");
                public static final Message SET_MATERIAL_SUCCESS =
                        m("cmd.arena.platform.set.material.success",
                          "$+You've successfully set Platform $v%s$+'s material type to $v%s$+.");
            }
        }

        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static final class Game {
            public static final Message INIT_SUCCESS =
                    m("cmd.game.init.success",
                      "$+You've successfully initialized a Game with the arena as $v%s$+.");
            public static final Message START_SUCCESS =
                    m("cmd.game.start.success", "$+You've successfully started the Game.");

            public static final Message NOT_INITIALIZED =
                    m("cmd.game.not_initialized", "$-The Game isn't initialized.");

            public static final Message ALREADY_INITIALIZED =
                    m("cmd.game.already_initialized", "$-The Game is already initialized.");

            public static final Message ALREADY_BEGUN = m("cmd.game.already_begun", "$-The Game has already begun.");

            public static final Message HAS_NOT_BEGUN = m("cmd.game.has_not_begun", "$-The Game hasn't begun.");

            public static final Message STOP_SUCCESS =
                    m("cmd.game.stop.success", "$+You've successfully stopped the Game.");

            public static final Message TIME_NO_ZERO =
                    m("cmd.game.time.no_zero", "$-You cannot modify the current time by zero.");

            public static final Message TIME_INCREASE_SUCCESS =
                    m("cmd.game.time.increase.success", "$+You've successfully increased the Game by $v%s$+ seconds.");

            public static final Message TIME_REDUCE_SUCCESS =
                    m("cmd.game.time.reduce.success", "$+You've successfully reduced the Game by $v%s$+ seconds.");

            public static final Message TIME_SET_SUCCESS =
                    m("cmd.game.time.set.success",
                      "$+You've successfully set the Game's remaining time to  $v%s$+ seconds.");
        }

        @NoArgsConstructor(access = AccessLevel.PRIVATE)
        public static final class General {

            public static final Message IGNORE_TRUE =
                    m("cmd.general.ignore.true",
                      "$+You're now ignored from the game.");
            
            public static final Message IGNORE_FALSE =
                    m("cmd.general.ignore.false",
                      "$+You're no longer ignored from the game.");
        }
    }
}
