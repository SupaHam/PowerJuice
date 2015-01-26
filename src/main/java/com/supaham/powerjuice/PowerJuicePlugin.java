package com.supaham.powerjuice;

import java.util.List;

import com.google.common.base.Joiner;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.supaham.powerjuice.arena.ArenaManager;
import com.supaham.powerjuice.arena.ArenaProperties;
import com.supaham.powerjuice.arena.ArenaProperties.GameProperties;
import com.supaham.powerjuice.arena.ArenaProperties.Sounds;
import com.supaham.powerjuice.arena.ArenaProperties.WeaponProperties;
import com.supaham.powerjuice.arena.ArenaProperties.WeaponProperties.SuperBow;
import com.supaham.powerjuice.commands.CommandsManager;
import com.supaham.powerjuice.game.GameManager;
import com.supaham.powerjuice.lobby.LobbyManager;
import com.supaham.powerjuice.lobby.LobbyProperties;
import com.supaham.powerjuice.platform.PlatformProperties;
import com.supaham.powerjuice.players.PlayerManager;
import com.supaham.powerjuice.powerup.PowerupProperties;
import com.supaham.powerjuice.powerup.PowerupProperties.VolleyShot;
import com.supaham.powerjuice.weapon.WeaponManager;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import pluginbase.bukkit.BukkitPluginAgent;
import pluginbase.config.SerializationRegistrar;
import pluginbase.logging.PluginLogger;
import pluginbase.plugin.PluginBase;

import static com.supaham.powerjuice.powerup.PowerupProperties.BoomShot;
import static com.supaham.powerjuice.powerup.PowerupProperties.IceShot;
import static com.supaham.powerjuice.powerup.PowerupProperties.RapidFire;

/**
 * Represents the main class for this plugin.
 */
@Getter
public class PowerJuicePlugin extends JavaPlugin {

    @Getter
    private static PowerJuicePlugin instance;
    private static final String COMMAND_PREFIX = "pj";

    static {
        SerializationRegistrar.registerClass(ArenaProperties.class);
        SerializationRegistrar.registerClass(GameProperties.class);
        SerializationRegistrar.registerClass(Sounds.class);
        SerializationRegistrar.registerClass(WeaponProperties.class);
        SerializationRegistrar.registerClass(SuperBow.class);
        
        SerializationRegistrar.registerClass(LobbyProperties.class);
        SerializationRegistrar.registerClass(PlatformProperties.class);
        SerializationRegistrar.registerClass(PowerupProperties.class);
        SerializationRegistrar.registerClass(BoomShot.class);
        SerializationRegistrar.registerClass(IceShot.class);
        SerializationRegistrar.registerClass(RapidFire.class);
        SerializationRegistrar.registerClass(VolleyShot.class);
    }

    @Getter(AccessLevel.PRIVATE)
    private final BukkitPluginAgent<PowerJuicePlugin> pluginAgent;
    
    private PlayerManager playerManager;
    private LobbyManager lobbyManager;
    private ArenaManager arenaManager;
    private WeaponManager weaponManager;
    
    private GameManager gameManager;
    
    private CommandsManager commandsManager;

    public PowerJuicePlugin() {
        PowerJuicePlugin.instance = this;
        this.pluginAgent = BukkitPluginAgent.getPluginAgent(PowerJuicePlugin.class, this, COMMAND_PREFIX);
    }

    @Override
    public void onLoad() {
        this.pluginAgent.loadPluginBase();
    }

    @Override
    public void onEnable() {
        this.pluginAgent.enablePluginBase();
        this.playerManager = new PlayerManager(this);
        this.lobbyManager = new LobbyManager(this);
        this.arenaManager = new ArenaManager(this);
        this.weaponManager = new WeaponManager(this);

        this.gameManager = new GameManager(this);

        this.commandsManager = new CommandsManager(this);
        this.commandsManager.registerCommands();
        
        regEvents(new PJListener(this));
        for (Player player : Bukkit.getOnlinePlayers()) {
            getPlayerManager().createPJPlayer(player);
        }
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String[] split = new String[args.length + 1];
        System.arraycopy(args, 0, split, 1, args.length);
        split[0] = command.getName();
        this.commandsManager.handleCommand(sender, Joiner.on(" ").join(split));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        String[] split = new String[args.length + 1];
        System.arraycopy(args, 0, split, 1, args.length);
        split[0] = command.getName();

        return this.commandsManager.handleCommandSuggestion(sender, Joiner.on(" ").join(split));
    }

    public void regEvents(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    public PluginBase<PowerJuicePlugin> getPluginBase() {
        return this.pluginAgent.getPluginBase();
    }

    public PluginLogger getLog() {
        return getPluginBase().getLog();
    }

    /**
     * Gets an instance of {@link WorldEditPlugin}.
     *
     * @return instance of WorldEditPlugin
     */
    public WorldEditPlugin getWorldEdit() {
        return (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
    }
}
