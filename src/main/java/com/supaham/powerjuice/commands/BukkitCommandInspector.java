package com.supaham.powerjuice.commands;

import com.sk89q.bukkit.util.CommandInspector;
import com.sk89q.minecraft.util.commands.CommandLocals;
import com.sk89q.worldedit.util.command.CommandMapping;
import com.sk89q.worldedit.util.command.Description;
import com.sk89q.worldedit.util.command.Dispatcher;
import com.supaham.powerjuice.PowerJuicePlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

class BukkitCommandInspector implements CommandInspector {
    
    private final PowerJuicePlugin plugin;
    private final Dispatcher dispatcher;

    public BukkitCommandInspector(@NotNull PowerJuicePlugin plugin, @NotNull Dispatcher dispatcher) {
        this.plugin = plugin;
        this.dispatcher = dispatcher;
    }

    @Override
    public String getShortText(Command command) {
        CommandMapping mapping = dispatcher.get(command.getName());
        if (mapping != null) {
            return mapping.getDescription().getShortDescription();
        } else {
            plugin.getLog().warning("BukkitCommandInspector doesn't know how about the command '" + command + "'");
            return "Help text not available";
        }
    }

    @Override
    public String getFullText(Command command) {
        CommandMapping mapping = dispatcher.get(command.getName());
        if (mapping != null) {
            Description description = mapping.getDescription();
            return "Usage: " + description.getUsage() + (description.getHelp() != null ? "\n" + description.getHelp() : "");
        } else {
            plugin.getLog().warning("BukkitCommandInspector doesn't know about the command '" + command + "'");
            return "Help text not available";
        }
    }

    @Override
    public boolean testPermission(CommandSender sender, Command command) {
        CommandMapping mapping = dispatcher.get(command.getName());
        if (mapping != null) {
            CommandLocals locals = new CommandLocals();
            locals.put(CommandSender.class, sender);
            return mapping.getCallable().testPermission(locals);
        } else {
            plugin.getLog().warning("BukkitCommandInspector doesn't know about the command '" + command + "'");
            return false;
        }
    }
}
