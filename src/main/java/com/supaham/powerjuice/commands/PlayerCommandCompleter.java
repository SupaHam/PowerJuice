package com.supaham.powerjuice.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandLocals;
import com.sk89q.worldedit.util.command.CommandCompleter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Provides the names of connected {@link Player}s as suggestions.
 */
public class PlayerCommandCompleter implements CommandCompleter {
    
    @Override
    public List<String> getSuggestions(String arguments, CommandLocals locals) throws CommandException {
        List<String> suggestions = new ArrayList<>();
        String l = arguments.toLowerCase().trim();
        suggestions.addAll(Bukkit.getOnlinePlayers().stream()
                                   .filter(player -> player.getName().toLowerCase().startsWith(l)).map(Player::getName)
                                   .collect(Collectors.toList()));
        return suggestions;
    }
}
