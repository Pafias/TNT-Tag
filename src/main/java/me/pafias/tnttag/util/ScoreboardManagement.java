package me.pafias.tnttag.util;

import me.pafias.tnttag.User;
import me.pafias.tnttag.Users;
import me.pafias.tnttag.game.Game;
import me.pafias.tnttag.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardManagement {

    public static void setScoreboard(Player player) {
        try {
            User user = Users.getUser(player);
            Game game = GameManager.getGame(user);
            if (user != null && game != null) {
                final ScoreboardManager scoreboardmanager = Bukkit.getScoreboardManager();
                final Scoreboard scoreboard = scoreboardmanager.getNewScoreboard();
                final Objective scoreboardobjective = scoreboard.registerNewObjective("TNT Tag", "dummy");
                scoreboardobjective.setDisplaySlot(DisplaySlot.SIDEBAR);
                scoreboardobjective.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "TNT Tag");
                scoreboardobjective.getScore(ChatColor.GRAY + "Round #" + game.round).setScore(9);
                scoreboardobjective.getScore("   ").setScore(8);
                scoreboardobjective.getScore(ChatColor.YELLOW + "Explosion in " + ChatColor.GREEN + game.roundSeconds + "s").setScore(7);
                scoreboardobjective.getScore("  ").setScore(6);
                scoreboardobjective.getScore(ChatColor.RESET + "Goal: " + (user.isTNT() ? ChatColor.RED + "Tag someone!" : ChatColor.GREEN + "Run away!")).setScore(5);
                scoreboardobjective.getScore(" ").setScore(4);
                List<User> alive = new ArrayList<>();
                for (User u : game.getPlayers())
                    if (u.getPlayer().getGameMode() != GameMode.SPECTATOR)
                        alive.add(u);
                scoreboardobjective.getScore(ChatColor.RESET + "Alive: " + ChatColor.GREEN + alive.size() + " Players").setScore(3);
                scoreboardobjective.getScore("").setScore(2);
                scoreboardobjective.getScore("not hypixel.net").setScore(1);
                player.setScoreboard(scoreboard);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void resetScoreboard(Player player) {
        try {
            final ScoreboardManager scoreboardmanager = Bukkit.getScoreboardManager();
            final Scoreboard scoreboard = scoreboardmanager.getNewScoreboard();
            final Objective scoreboardobjective = scoreboard.registerNewObjective("TNT Tag", "dummy");
            scoreboardobjective.setDisplaySlot(DisplaySlot.SIDEBAR);
            scoreboardobjective.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "TNT Tag");
            scoreboardobjective.unregister();
            player.setScoreboard(scoreboard);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}