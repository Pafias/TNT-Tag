package me.pafias.tnttag.config;

import me.pafias.tnttag.TNTTag;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class GameConfig {

    String s;
    java.io.File File;
    FileConfiguration Config;

    public GameConfig(String name) {
        this.s = name;
        File = new File(TNTTag.getInstance().getDataFolder() + "//gamedata//", s + ".yml");
        Config = YamlConfiguration.loadConfiguration(File);
    }

    public void createConfig() {
        if (!(File.exists())) {
            try {
                YamlConfiguration UserConfig = YamlConfiguration.loadConfiguration(File);
                UserConfig.set("name", s);
                UserConfig.save(File);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public FileConfiguration getConfig() {
        return Config;
    }

    public void saveConfig() {
        try {
            getConfig().save(File);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
