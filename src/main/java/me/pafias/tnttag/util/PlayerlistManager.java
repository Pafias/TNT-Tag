package me.pafias.tnttag.util;

import me.pafias.tnttag.User;
import me.pafias.tnttag.Users;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayerlistManager {

    public static void updateTabList(Player player) {
        try {
            User user = Users.getUser(player);
            player.setPlayerListName((user.isTNT() ? ChatColor.RED + "[IT] " : ChatColor.GREEN) + user.getPlayer().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void resetTabList(Player player) {
        try {
            player.setPlayerListName(player.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}