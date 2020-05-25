package me.pafias.tnttag;

import me.pafias.tnttag.config.UserConfig;
import me.pafias.tnttag.game.Game;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class User {

    private Player player;
    private UserConfig config;

    private boolean inGame;
    private boolean isTNT;
    private int totalWins;
    private int totalPoints;
    private int totalHits;
    private int totalBlocksWalked;

    public User(Player player) {
        this.player = player;
        this.config = new UserConfig(player.getUniqueId());
        this.totalWins = config.getConfig().getInt("totalWins");
        this.totalPoints = config.getConfig().getInt("totalPoints");
        this.totalHits = config.getConfig().getInt("totalHits");
        this.totalBlocksWalked = config.getConfig().getInt("totalBlocksWalked");
    }

    public Player getPlayer() {
        return player;
    }

    public UserConfig getConfig() {
        return config;
    }

    public int getTotalWins() {
        return totalWins;
    }

    public void setTotalWins(int totalWins) {
        this.totalWins = totalWins;
        config.getConfig().set("totalWins", totalWins);
        config.saveConfig();
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
        config.getConfig().set("totalPoints", totalPoints);
        config.saveConfig();
    }

    public int getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(int totalHits) {
        this.totalHits = totalHits;
        config.getConfig().set("totalHits", totalHits);
        config.saveConfig();
    }

    public int getTotalBlocksWalked() {
        return totalBlocksWalked;
    }

    public void setTotalBlocksWalked(int totalBlocksWalked) {
        this.totalBlocksWalked = totalBlocksWalked;
        config.getConfig().set("totalBlocksWalked", totalBlocksWalked);
        config.saveConfig();
    }

    public boolean isInGame() {
        return inGame;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    public boolean isTNT() {
        return isTNT;
    }

    public void setTNT(boolean TNT, Game game) {
        isTNT = TNT;
        if (isTNT) {
            game.broadcast(ChatColor.GRAY + player.getName() + ChatColor.RED + " is IT!");
            for (PotionEffect pe : player.getActivePotionEffects())
                player.removePotionEffect(pe.getType());
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 1, false, false));
            player.getInventory().setHelmet(new ItemStack(Material.TNT, 1));
            for (int i = 0; i < 9; i++)
                player.getInventory().setItem(i, new ItemStack(Material.TNT, 1));
        } else {
            for (PotionEffect pe : player.getActivePotionEffects())
                player.removePotionEffect(pe.getType());
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 0, false, false));
            player.getInventory().setHelmet(null);
            for (int i = 0; i < 9; i++)
                player.getInventory().setItem(i, null);
        }
    }
}
