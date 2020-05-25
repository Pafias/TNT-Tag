package me.pafias.tnttag;

import me.pafias.tnttag.commands.TNTTagCommand;
import me.pafias.tnttag.game.Game;
import me.pafias.tnttag.game.GameManager;
import me.pafias.tnttag.game.GameState;
import me.pafias.tnttag.listeners.GUIListener;
import me.pafias.tnttag.listeners.GameListener;
import me.pafias.tnttag.listeners.JoinAndQuitListener;
import me.pafias.tnttag.util.PlayerlistManager;
import me.pafias.tnttag.util.ScoreboardManagement;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class TNTTag extends JavaPlugin {

    private static final String ALPHA_NUMERIC_STRING = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static TNTTag instance;

    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    public static TNTTag getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        for (Player p : getServer().getOnlinePlayers())
            Users.addUser(p);
        registerCommands();
        registerListeners();
        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                User user = Users.getUser(p);
                if (user.isInGame()) {
                    Game game = GameManager.getGame(user);
                    PlayerlistManager.updateTabList(user.getPlayer());
                    getServer().getScheduler().scheduleSyncDelayedTask(TNTTag.getInstance(), () -> {
                        if (game.getState() == GameState.INGAME)
                            ScoreboardManagement.setScoreboard(user.getPlayer());
                    }, 2);
                }
            }
        }, 20, 10);
    }

    private void registerCommands() {
        getCommand("tnttag").setExecutor(new TNTTagCommand());
    }

    private void registerListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new JoinAndQuitListener(), this);
        pm.registerEvents(new GameListener(), this);
        pm.registerEvents(new GUIListener(), this);
    }

    @Override
    public void onDisable() {
        if (!GameManager.getGames().isEmpty())
            for (Game game : GameManager.getGames().values())
                game.stop();
        instance = null;
    }

    public Location getLobby() {
        return new Location(getServer().getWorld(getConfig().getString("lobby.world")), getConfig().getDouble("lobby.x"), getConfig().getDouble("lobby.y"), getConfig().getDouble("lobby.z"), (float) getConfig().getDouble("lobby.yaw"), (float) getConfig().getDouble("lobby.pitch"));
    }
}
