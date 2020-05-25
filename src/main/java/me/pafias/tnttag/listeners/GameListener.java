package me.pafias.tnttag.listeners;

import me.pafias.tnttag.User;
import me.pafias.tnttag.Users;
import me.pafias.tnttag.game.Game;
import me.pafias.tnttag.game.GameManager;
import me.pafias.tnttag.game.GameState;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class GameListener implements Listener {

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            User damaged = Users.getUser((Player) event.getEntity());
            Game game = GameManager.getGame(damaged);
            if (game.getState() != GameState.INGAME)
                event.setCancelled(true);
            User damager = Users.getUser((Player) event.getDamager());
            if (damaged.isInGame() && damager.isInGame() && damager.isTNT()) {
                damager.setTNT(false, GameManager.getGame(damager));
                damaged.setTNT(true, GameManager.getGame(damaged));
                damaged.getPlayer().sendMessage(ChatColor.GRAY + damager.getPlayer().getName() + ChatColor.RED + " tagged you!");
                damager.getPlayer().sendMessage(ChatColor.GREEN + "You tagged " + ChatColor.GRAY + damaged.getPlayer().getName() + ChatColor.GREEN + "!");
                damager.setTotalHits(damager.getTotalHits() + 1);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            User user = Users.getUser((Player) event.getEntity());
            if (user.isInGame())
                event.setDamage(0);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlock() != event.getTo().getBlock()) {
            User user = Users.getUser(event.getPlayer());
            user.setTotalBlocksWalked(user.getTotalBlocksWalked() + 1);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (Users.getUser(event.getPlayer()).isInGame())
            event.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (Users.getUser(event.getPlayer()).isInGame())
            event.setCancelled(true);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        if (Users.getUser(event.getPlayer()).isInGame())
            event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (Users.getUser(event.getPlayer()).isInGame())
            event.setCancelled(true);
    }

}
