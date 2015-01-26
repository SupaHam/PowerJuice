package com.supaham.powerjuice.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.util.command.parametric.ExceptionConverterHelper;
import com.sk89q.worldedit.util.command.parametric.ExceptionMatch;
import com.supaham.powerjuice.PJException;
import com.supaham.powerjuice.PowerJuicePlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Converts non {@link CommandException}s into CommandExceptions.
 */
public class PJExceptionConverter extends ExceptionConverterHelper {

    private static final Pattern numberFormat = Pattern.compile("^For input string: \"(.*)\"$");
    private final PowerJuicePlugin plugin;

    public PJExceptionConverter(@NotNull PowerJuicePlugin plugin) {
        this.plugin = plugin;
    }

    @ExceptionMatch
    public void convert(NumberFormatException e) throws CommandException {
        final Matcher matcher = numberFormat.matcher(e.getMessage());
        if (matcher.matches()) {
            throw new CommandException("Number expected; string \"" + matcher.group(1) + "\" given.");
        } else {
            throw new CommandException("Number expected; string given.");
        }
    }
    
    @ExceptionMatch
    public void convert(IncompleteRegionException e) throws CommandException {
        throw new CommandException("Make a region selection first.");
    }

    @ExceptionMatch
    public void convert(PJException e) throws CommandException {
        throw new CommandException(e.getMessage(), e);
    }
}
