package me.pafias.tnttag.game;

import me.pafias.tnttag.TNTTag;
import me.pafias.tnttag.User;
import me.pafias.tnttag.config.GameConfig;
import me.pafias.tnttag.util.*;
import org.bukkit.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game {

    public boolean started = false;
    public int round;
    public int roundSeconds;
    int fireworkINT = 5;
    private String id;
    private GameConfig config;
    private String name;
    private World world;
    private GameState gamestate;
    private int minPlayers;
    private int maxPlayers;
    private List<User> players;
    private List<Material> pickups;
    private Location spawn;
    private Location lobbyspawn;
    private int time;
    private int taskID;
    private int roundsTask;
    private BukkitScheduler scheduler = TNTTag.getInstance().getServer().getScheduler();

    public Game(String id, World world) {
        this.id = id;
        this.config = new GameConfig(world.getName().split("_")[0]);
        this.name = config.getConfig().getString("name");
        this.world = world;
        world.setDifficulty(Difficulty.PEACEFUL);
        world.setTime(1600);
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setGameRuleValue("doWeatherCycle", "false");
        this.gamestate = GameState.LOBBY;
        this.minPlayers = config.getConfig().getInt("minPlayers");
        this.maxPlayers = config.getConfig().getInt("maxPlayers");
        this.players = new ArrayList<>();
        this.round = 0;
        this.spawn = new Location(Bukkit.getWorld(config.getConfig().getString("spawn.world") + "_" + id),
                config.getConfig().getDouble("spawn.x"),
                config.getConfig().getDouble("spawn.y"),
                config.getConfig().getDouble("spawn.z"),
                (float) config.getConfig().getDouble("spawn.yaw"),
                (float) config.getConfig().getDouble("spawn.pitch"));
        this.lobbyspawn = new Location(Bukkit.getWorld(config.getConfig().getString("lobbyspawn.world") + "_" + id),
                config.getConfig().getDouble("lobbyspawn.x"),
                config.getConfig().getDouble("lobbyspawn.y"),
                config.getConfig().getDouble("lobbyspawn.z"),
                (float) config.getConfig().getDouble("lobbyspawn.yaw"),
                (float) config.getConfig().getDouble("lobbyspawn.pitch"));
    }

    public void start(boolean force) {
        if (!started) {
            if (players.size() >= minPlayers || force) {
                started = true;
                setGamestate(GameState.PREGAME);
                time = 30;
                taskID = scheduler.scheduleSyncRepeatingTask(TNTTag.getInstance(), () -> {
                    if (time == 0) {
                        Bukkit.getScheduler().cancelTask(taskID);
                        setGamestate(GameState.INGAME);
                        broadcast(ChatColor.GREEN + "Game started!");
                        for (User p : getPlayers()) {
                            p.getPlayer().teleport(spawn);
                            p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.BLOCK_NOTE_PLING, 1F, 1F);
                            p.getPlayer().setExp(0);
                            p.getPlayer().setLevel(0);
                            p.getPlayer().setGameMode(GameMode.SURVIVAL);
                            p.getPlayer().setInvulnerable(false);
                            for (PotionEffect pe : p.getPlayer().getActivePotionEffects())
                                p.getPlayer().removePotionEffect(pe.getType());
                            p.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 0, false, false));
                        }
                        handleRounds();
                        handleTNT();
                        return;
                    }
                    for (User all : getPlayers()) {
                        all.getPlayer().setLevel(time);
                        all.getPlayer().setExp(time / (float) 30);
                    }
                    if (time == 10 || time == 5 || time == 4 || time == 3 || time == 2 || time == 1) {
                        for (User p : getPlayers()) {
                            p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.BLOCK_NOTE_PLING, 1F, 1F);
                        }
                        broadcast(ChatColor.RED + "The game will start in " + time + " seconds!");
                    }
                    time = time - 1;
                }, 0, 20);
            }
        }
    }

    private void handleRounds() {
        if (round == 0)
            round++;
        List<User> p = new ArrayList<>();
        for (User u : players)
            if (u.getPlayer().getGameMode() != GameMode.SPECTATOR)
                p.add(u);
        broadcast(ChatColor.WHITE + "" + ChatColor.BOLD + "Round " + round + " has started!");
        roundSeconds = p.size() >= 10 ? 50 : p.size() < 10 && p.size() >= 6 ? 15 : p.size() < 6 ? 15 : 15;
        roundsTask = scheduler.scheduleSyncRepeatingTask(TNTTag.getInstance(), () -> {
            if (roundSeconds == 1) {
                Bukkit.getScheduler().cancelTask(roundsTask);
                for (User u : players)
                    if (u.isTNT()) {
                        u.getPlayer().getLocation().getWorld().playEffect(u.getPlayer().getLocation(), Effect.SMOKE, 0, 5);
                        u.getPlayer().getLocation().getWorld().createExplosion(u.getPlayer().getEyeLocation().getX(), u.getPlayer().getEyeLocation().getY(), u.getPlayer().getEyeLocation().getZ(), 4.0F, false, false);
                        broadcast(ChatColor.GRAY + u.getPlayer().getName() + ChatColor.RED + " blew up!");
                        u.setTNT(false, this);
                        u.getPlayer().setGameMode(GameMode.SPECTATOR);
                        p.remove(u);
                    } else {
                        u.setTotalPoints(u.getTotalPoints() + 1);
                    }
                scheduler.runTaskLater(TNTTag.getInstance(), () -> {
                    if (p.size() == 1) {
                        scheduler.cancelTasks(TNTTag.getInstance());
                        broadcast("");
                        broadcast(ChatColor.GRAY + p.get(0).getPlayer().getName() + ChatColor.GOLD + " was the last survivor and won the game!");
                        broadcast("");
                        for (User u : players)
                            for (PotionEffect pe : u.getPlayer().getActivePotionEffects())
                                u.getPlayer().removePotionEffect(pe.getType());
                        setGamestate(GameState.POSTGAME);
                        scheduler.scheduleSyncRepeatingTask(TNTTag.getInstance(),
                                () -> {
                                    TNTTag.getInstance().getServer().getScheduler().runTaskLater(TNTTag.getInstance(),
                                            () -> {
                                                if (p.get(0) != null) Fireworks.spawnFirework(p.get(0));
                                            }, 20);
                                    fireworkINT--;
                                }, 0, 20);
                        scheduler.runTaskLater(TNTTag.getInstance(), this::stop, (7 * 20));
                        return;
                    } else if (p.isEmpty()) {
                        broadcast("");
                        broadcast(ChatColor.RED + "Nobody" + ChatColor.GOLD + " won the game. weird...");
                        broadcast("");
                        scheduler.runTaskLater(TNTTag.getInstance(), this::stop, (5 * 20));
                        return;
                    }
                    round++;
                    handleRounds();
                    if (p.size() <= 6)
                        handleDMTeleport();
                    handleTNT();
                }, (5 * 20));
            }
            for (User all : getPlayers()) {
                all.getPlayer().setExp(roundSeconds / (float) 30);
            }
            roundSeconds--;
        }, 2, 20);
    }

    private void handleDMTeleport() {
        for (User u : players)
            u.getPlayer().teleport(spawn);
    }

    private void handleTNT() {
        List<User> p = new ArrayList<>();
        for (User u : players)
            if (u.getPlayer().getGameMode() != GameMode.SPECTATOR)
                p.add(u);
        List<User> it = new ArrayList<>();
        if (p.size() >= 20) {
            for (int i = 0; i < 6; i++) {
                User u = p.get(new Random().nextInt(p.size()));
                u.setTNT(true, this);
                p.remove(u);
                it.add(u);
            }
        }
        if (p.size() < 20 && p.size() >= 10) {
            for (int i = 0; i < 3; i++) {
                User u = p.get(new Random().nextInt(p.size()));
                u.setTNT(true, this);
                p.remove(u);
                it.add(u);
            }
        } else if (p.size() < 10 && p.size() >= 6) {
            for (int i = 0; i < 2; i++) {
                User u = p.get(new Random().nextInt(p.size()));
                u.setTNT(true, this);
                p.remove(u);
                it.add(u);
            }
        } else if (p.size() < 6) {
            for (int i = 0; i < 1; i++) {
                User u = p.get(new Random().nextInt(p.size()));
                u.setTNT(true, this);
                p.remove(u);
                it.add(u);
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < it.size(); i++) {
            sb.append(ChatColor.GRAY + it.get(i).getPlayer().getName()).append(it.indexOf(i) == it.size() - 1 ? ChatColor.YELLOW + " and " : it.indexOf(i) == it.size() ? ChatColor.YELLOW + ", " : "");
        }
        broadcast(" ");
        broadcast(ChatColor.YELLOW + "The TNT has been released to " + ChatColor.RESET + sb.toString() + ChatColor.YELLOW + "!");
        broadcast(" ");
        for (User u : players)
            if (u.getPlayer().getGameMode() != GameMode.SPECTATOR)
                if (!u.isTNT())
                    ActionBar.sendActionbar(u.getPlayer(), ChatColor.GREEN + "Run away!");
                else
                    ActionBar.sendActionbar(u.getPlayer(), ChatColor.RED + "You're IT, tag someone!");
    }

    public void stop() {
        for (User user : getPlayers()) {
            user.setTNT(false, this);
            user.getPlayer().getInventory().clear();
            for (PotionEffect pe : user.getPlayer().getActivePotionEffects())
                user.getPlayer().removePotionEffect(pe.getType());
            user.getPlayer().setExp(0);
            user.getPlayer().setLevel(0);
            user.getPlayer().setGameMode(GameMode.SURVIVAL);
            user.getPlayer().setInvulnerable(false);
            user.setInGame(false);
            user.getPlayer().showPlayer(user.getPlayer());
            PlayerlistManager.resetTabList(user.getPlayer());
            ScoreboardManagement.resetScoreboard(user.getPlayer());
            user.getPlayer().teleport(TNTTag.getInstance().getLobby());
        }
        world.getEntities().clear();
        Bukkit.getScheduler().cancelTasks(TNTTag.getInstance());
        TNTTag.getInstance().getServer().getScheduler().runTaskLater(TNTTag.getInstance(), () -> RollbackHandler.delete(world.getName()), 60);
        GameManager.removeGame(this);
    }

    public void broadcast(String message) {
        for (User user : getPlayers())
            user.getPlayer().sendMessage(message);
    }

    public String getID() {
        return id;
    }

    public GameConfig getConfig() {
        return config;
    }

    public String getName() {
        return name;
    }

    public World getWorld() {
        return world;
    }

    public GameState getState() {
        return gamestate;
    }

    public void setGamestate(GameState gamestate) {
        this.gamestate = gamestate;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public List<User> getPlayers() {
        return players;
    }

    public void setPlayers(List<User> players) {
        this.players = players;
    }

    public Location getSpawn() {
        return spawn;
    }

    public Location getLobbySpawn() {
        return lobbyspawn;
    }

}
