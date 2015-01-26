package com.supaham.powerjuice.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Joiner;
import com.sk89q.bukkit.util.CommandInfo;
import com.sk89q.bukkit.util.CommandRegistration;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandLocals;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.minecraft.util.commands.WrappedCommandException;
import com.sk89q.worldedit.util.command.CommandMapping;
import com.sk89q.worldedit.util.command.Description;
import com.sk89q.worldedit.util.command.Dispatcher;
import com.sk89q.worldedit.util.command.InvalidUsageException;
import com.sk89q.worldedit.util.command.fluent.CommandGraph;
import com.sk89q.worldedit.util.command.parametric.LegacyCommandsHandler;
import com.sk89q.worldedit.util.command.parametric.ParametricBuilder;
import com.sk89q.worldedit.util.formatting.ColorCodeBuilder;
import com.sk89q.worldedit.util.formatting.component.CommandUsageBox;
import com.supaham.powerjuice.PowerJuicePlugin;
import com.supaham.powerjuice.commands.arena.ArenaAddCommands;
import com.supaham.powerjuice.commands.arena.ArenaCommands;
import com.supaham.powerjuice.commands.arena.ArenaRemoveCommands;
import com.supaham.powerjuice.commands.arena.ArenaSetCommands;
import com.supaham.powerjuice.commands.arena.platform.PlatformCommands;
import com.supaham.powerjuice.commands.arena.platform.PlatformSetCommands;
import com.supaham.powerjuice.commands.game.GameCommands;
import com.supaham.powerjuice.commands.gamersession.GamerSessionCommands;
import com.supaham.powerjuice.commands.lobby.LobbyAddCommands;
import com.supaham.powerjuice.commands.lobby.LobbyCommands;
import com.supaham.powerjuice.commands.lobby.LobbyRemoveCommands;
import com.supaham.powerjuice.commands.lobby.LobbySetCommands;
import com.supaham.powerjuice.players.PJPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Handles the registration and invocation of commands.
 */
public class CommandsManager {

    private final PowerJuicePlugin plugin;
    private final Dispatcher dispatcher;
    private CommandRegistration dynamicCommands;

    public CommandsManager(@NotNull PowerJuicePlugin plugin) {
        this.plugin = plugin;
        dynamicCommands = new CommandRegistration(plugin);

        ParametricBuilder builder = new ParametricBuilder();
        builder.setAuthorizer(new CommandAuthorizer());
        builder.setDefaultCompleter(new PlayerCommandCompleter());
        builder.addBinding(new PJBinding(plugin));
        builder.addExceptionConverter(new PJExceptionConverter(plugin));
        builder.addInvokeListener(new LegacyCommandsHandler());

        // @formatter:off
        this.dispatcher = new CommandGraph()
                .builder(builder)
                    .commands()
                    .registerMethods(new GeneralCommands(plugin))
                    .group("arena", "ar")
                        .describeAs("Arena management commands.")
                        .registerMethods(new ArenaCommands(plugin))
                        // Add 
                        .group("add")
                            .describeAs("Arena add commands.")
                            .registerMethods(new ArenaAddCommands(plugin))
                            .parent()
                        // Remove 
                        .group("remove")
                            .describeAs("Arena remove commands.")
                            .registerMethods(new ArenaRemoveCommands(plugin))
                            .parent() // arena group
                        // Set
                        .group("set")
                            .describeAs("Arena set commands.")
                            .registerMethods(new ArenaSetCommands(plugin))
                            .parent() // arena group
                        // Platform
                        .group("platform")
                            .describeAs("Platform management commands.")
                            .registerMethods(new PlatformCommands(plugin))
                            // Platform Set
                            .group("set")
                                .describeAs("Arena Platform set commands.")
                                .registerMethods(new PlatformSetCommands(plugin))
                                .parent() // platform group
                            .parent() // arena group
                        .parent() // root
                    .group("lobby", "l")
                        .describeAs("Lobby management commands.")
                        .registerMethods(new LobbyCommands(plugin))
                        // Add 
                        .group("add")
                            .describeAs("Lobby add commands.")
                            .registerMethods(new LobbyAddCommands(plugin))
                            .parent() // lobby group
                        // Remove 
                        .group("remove")
                            .describeAs("Lobby remove commands.")
                            .registerMethods(new LobbyRemoveCommands(plugin))
                            .parent() // lobby group
                        // Set
                        .group("set")
                            .describeAs("Lobby set commands.")
                            .registerMethods(new LobbySetCommands(plugin))
                            .parent() // lobby group
                        .parent() // root
                    .group("game", "ga")
                        .describeAs("Game management commands.")
                        .registerMethods(new GameCommands(plugin))
                        .parent() // root
                    .group("gamersession", "gs")
                        .describeAs("GamerSession management commands.")
                        .registerMethods(new GamerSessionCommands(plugin))
                        .parent() // root
                .graph().getDispatcher();
        // @formatter:on
    }

    public void handleCommand(CommandSender sender, String arguments) {

        String[] split = arguments.split(" ");

        // No command found!
        if (!dispatcher.contains(split[0])) {
            return;
        }

        CommandLocals locals = new CommandLocals();

        boolean isPlayer = sender instanceof Player;
        String RED = isPlayer ? ChatColor.RED.toString() : "";

        locals.put(CommandSender.class, sender);
        if (isPlayer) {
            locals.put(Player.class, sender);
            locals.put(PJPlayer.class, plugin.getPlayerManager().getPJPlayer(sender));
        }

        try {
            dispatcher.call(Joiner.on(" ").join(split), locals, new String[0]);
        } catch (CommandPermissionsException e) {
            sender.sendMessage(RED + "You don't have permission to do this.");
        } catch (InvalidUsageException e) {
            if (e.isFullHelpSuggested()) {
                sender.sendMessage(ColorCodeBuilder.asColorCodes(
                        new CommandUsageBox(e.getCommand(), e.getCommandUsed("/", ""), locals)));
                String message = e.getMessage();
                if (message != null) {
                    sender.sendMessage(RED + message);
                }
            } else {
                String message = e.getMessage();
                sender.sendMessage(RED + (message != null ? message :
                                          "The command was not used properly (no more help available)."));
                sender.sendMessage(RED + "Usage: " + e.getSimpleUsageString("/"));
            }
        } catch (WrappedCommandException e) {
            Throwable t = e.getCause();
            sender.sendMessage(RED + "Please report this error: [See console]");
            sender.sendMessage(RED + t.getClass().getName() + ": " + t.getMessage());
            plugin.getLog().severe("An unexpected error while handling a PowerJuice command");
            t.printStackTrace();
        } catch (CommandException e) {
            String message = e.getMessage();
            if (message != null) {
                sender.sendMessage(RED + e.getMessage());
            } else {
                if (isPlayer) {
                    sender.sendMessage(RED + "An unknown error has occurred! Please report this to a staff " +
                                       "member immediately.");
                }
                plugin.getLog().severe("An unknown error occurred: ");
                e.printStackTrace();
            }
        }
    }

    public List<String> handleCommandSuggestion(CommandSender sender, String arguments) {
        try {
            return new PlayerCommandCompleter().getSuggestions(arguments, new CommandLocals());
        } catch (CommandException e) {
            sender.sendMessage(((sender instanceof Player) ? ChatColor.RED : "") + e.getMessage());
            return Collections.emptyList();
        }
    }

    public void registerCommands() {
        List<CommandInfo> toRegister = new ArrayList<CommandInfo>();
        BukkitCommandInspector inspector = new BukkitCommandInspector(plugin, dispatcher);

        for (CommandMapping command : dispatcher.getCommands()) {
            Description description = command.getDescription();
            List<String> permissions = description.getPermissions();
            String[] permissionsArray = new String[permissions.size()];
            permissions.toArray(permissionsArray);

            toRegister.add(new CommandInfo(description.getUsage(), description.getShortDescription(),
                                           command.getAllAliases(), inspector, permissionsArray));
        }

        dynamicCommands.register(toRegister);
    }
}
