package me.pafias.tnttag.listeners;

import me.pafias.tnttag.User;
import me.pafias.tnttag.Users;
import me.pafias.tnttag.config.UserConfig;
import me.pafias.tnttag.game.GameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinAndQuitListener implements Listener {

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        UserConfig config = new UserConfig(event.getPlayer().getUniqueId());
        config.createConfig(event.getPlayer());
        config.getConfig().set("name", event.getPlayer().getName());
        config.saveConfig();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Users.addUser(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        User user = Users.getUser(event.getPlayer());
        if (user.isInGame())
            GameManager.leaveGame(user);
        Users.removeUser(event.getPlayer());
    }

}
