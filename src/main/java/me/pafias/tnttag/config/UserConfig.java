package me.pafias.tnttag.config;

import me.pafias.tnttag.TNTTag;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class UserConfig {

    UUID uuid;
    File file;
    FileConfiguration config;

    public UserConfig(UUID uuid) {
        this.uuid = uuid;
        this.file = new File(TNTTag.getInstance().getDataFolder() + "//playerdata//", uuid.toString() + ".yml");
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void createConfig(Player player) {
        if (!file.exists()) {
            try {
                config.set("name", player.getName());
                config.set("totalWins", 0);
                config.save(file);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void saveConfig() {
        try {
            config.save(file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
