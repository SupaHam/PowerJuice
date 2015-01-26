package com.supaham.powerjuice.commands;

import com.sk89q.minecraft.util.commands.CommandLocals;
import com.sk89q.worldedit.util.auth.Authorizer;
import org.bukkit.command.CommandSender;

public class CommandAuthorizer implements Authorizer {
    
    @Override
    public boolean testPermission(CommandLocals locals, String permission) {
        return locals.get(CommandSender.class).hasPermission(permission);
    }
}
