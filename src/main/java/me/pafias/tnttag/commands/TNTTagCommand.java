package me.pafias.tnttag.commands;

import me.pafias.tnttag.User;
import me.pafias.tnttag.Users;
import me.pafias.tnttag.config.UserConfig;
import me.pafias.tnttag.game.Game;
import me.pafias.tnttag.game.GameManager;
import me.pafias.tnttag.game.GameState;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class TNTTagCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GOLD + "------------ TNT Tag ------------");
            sender.sendMessage(ChatColor.DARK_AQUA + "/tt create <world>");
            sender.sendMessage(ChatColor.DARK_AQUA + "/tt stats [player]");
            sender.sendMessage(ChatColor.DARK_AQUA + "/tt games");
            sender.sendMessage(ChatColor.DARK_AQUA + "/tt join <game id>");
            sender.sendMessage(ChatColor.DARK_AQUA + "/tt stop [game id]");
            sender.sendMessage(ChatColor.DARK_AQUA + "/tt forcestart");
            sender.sendMessage(ChatColor.DARK_AQUA + "/tt leave");
            sender.sendMessage(ChatColor.DARK_AQUA + "/tt gui");
            sender.sendMessage(ChatColor.GOLD + "----------------------------------");
            return true;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("stats")) {
                User user = Users.getUser((Player) sender);
                user.getPlayer().sendMessage(ChatColor.GOLD + "------------ TNT Tag ------------");
                user.getPlayer().sendMessage(ChatColor.GOLD + "Your total wins: " + ChatColor.LIGHT_PURPLE + user.getTotalWins());
                user.getPlayer().sendMessage(ChatColor.GOLD + "---------------------------------");
            } else if (args[0].equalsIgnoreCase("games")) {
                if (GameManager.getGames().values().isEmpty()) {
                    sender.sendMessage(ChatColor.RED + "No games available at the moment.");
                    return true;
                }
                sender.sendMessage(ChatColor.GOLD + "Join a game by clicking on the game you want below or by using /tt join <ID>");
                for (Game game : GameManager.getGames().values())
                    sender.sendMessage(new ComponentBuilder(ChatColor.GOLD + "ID: " + ChatColor.AQUA + game.getID() + ChatColor.GOLD + " Map: " + ChatColor.DARK_PURPLE + ChatColor.translateAlternateColorCodes('&', game.getName())).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tt join " + game.getID())).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.WHITE + "/tt join " + game.getID()).create())).create());
            } else if (args[0].equalsIgnoreCase("forcestart") && sender.isOp()) {
                User user = Users.getUser((Player) sender);
                if (!user.isInGame())
                    sender.sendMessage(ChatColor.RED + "You are not in a game.");
                else {
                    Game game = GameManager.getGame(Users.getUser((Player) sender));
                    game.start(true);
                    sender.sendMessage(ChatColor.GREEN + "You force started the game.");
                }
            } else if (args[0].equalsIgnoreCase("stop") && sender.isOp()) {
                User user = Users.getUser((Player) sender);
                if (!user.isInGame())
                    sender.sendMessage(ChatColor.RED + "You are not in a game.");
                else {
                    Game game = GameManager.getGame(user);
                    game.stop();
                    sender.sendMessage(ChatColor.GOLD + "Game stopped.");
                }
            } else if (args[0].equalsIgnoreCase("leave")) {
                User user = Users.getUser((Player) sender);
                if (user.isInGame())
                    GameManager.leaveGame(user);
                else
                    sender.sendMessage(ChatColor.RED + "You are not in a game!");
            } else if (args[0].equalsIgnoreCase("gui")) {
                User user = Users.getUser((Player) sender);
                if (GameManager.getGames().isEmpty()) {
                    user.getPlayer()
                            .sendMessage(ChatColor.RED + "There are currently no games available to join.");
                    return true;
                }
                int size = GameManager.getGames().size() <= 9 ? 9 : GameManager.getGames().size() <= 18 ? 18 : GameManager.getGames().size() <= 27 ? 27 : 54;
                Inventory inv = Bukkit.createInventory(null, size, ChatColor.GOLD + "TNT-Tag Game Selection");
                for (Game game : GameManager.getGames().values()) {
                    if (game.getState().equals(GameState.LOBBY) || game.getState().equals(GameState.PREGAME)) {
                        ItemStack is = new ItemStack(Material.WOOL, 1, DyeColor.LIME.getWoolData());
                        ItemMeta meta = is.getItemMeta();
                        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', game.getName()));
                        meta.setLore(Arrays.asList("",
                                ChatColor.GOLD + "Players: " + ChatColor.GRAY + game.getPlayers().size()
                                        + ChatColor.GOLD + "/" + ChatColor.GRAY + game.getMaxPlayers(),
                                "", ChatColor.GREEN + "Click here to join this game!", ""));
                        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                        meta.setLocalizedName(game.getID());
                        is.setItemMeta(meta);
                        inv.addItem(is);
                    }
                }
                user.getPlayer().openInventory(inv);
            }
            return true;
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("create") && sender.isOp()) {
                String game = GameManager.createGame(args[1]);
                sender.sendMessage(ChatColor.GREEN + "Game with ID " + ChatColor.LIGHT_PURPLE + game + ChatColor.GREEN + " created!");
            } else if (args[0].equalsIgnoreCase("stats")) {
                if (Bukkit.getPlayer(args[1]) != null) {
                    User user = Users.getUser(Bukkit.getPlayer(args[1]));
                    sender.sendMessage(ChatColor.GOLD + "------------ TNT Tag ------------");
                    sender.sendMessage(ChatColor.GRAY + user.getPlayer().getName() + ChatColor.GOLD + (user.getPlayer().getName().endsWith("s") ? "'" : "'s") + " total wins: " + ChatColor.LIGHT_PURPLE + user.getTotalWins());
                    sender.sendMessage(ChatColor.GOLD + "---------------------------------");
                } else {
                    UserConfig config = new UserConfig(Bukkit.getOfflinePlayer(args[1]).getUniqueId());
                    sender.sendMessage(ChatColor.GRAY + config.getConfig().getString("name") + ChatColor.GOLD + (config.getConfig().getString("name").endsWith("s") ? "'" : "'s") + " total wins: " + ChatColor.LIGHT_PURPLE + config.getConfig().getInt("totalWins"));
                }
            } else if (args[0].equalsIgnoreCase("join")) {
                User user = Users.getUser((Player) sender);
                if (GameManager.getGames().containsKey(args[1]))
                    if (!user.isInGame())
                        GameManager.joinGame(Users.getUser((Player) sender), args[1]);
                    else
                        sender.sendMessage(ChatColor.RED + "You are already in a game!");
                else
                    sender.sendMessage(ChatColor.RED + "Game not found");
            } else if (args[0].equalsIgnoreCase("stop") && sender.isOp()) {
                if (!GameManager.getGames().containsKey(args[1])) {
                    sender.sendMessage(ChatColor.RED + "Game not found!");
                    return true;
                }
                Game game = GameManager.getGames().get(args[1]);
                game.stop();
                sender.sendMessage(ChatColor.GOLD + "Game stopped.");
            }
            return true;
        }
        return true;
    }

}
