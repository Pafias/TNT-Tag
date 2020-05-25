package me.pafias.tnttag.util;

import me.pafias.tnttag.TNTTag;
import me.pafias.tnttag.User;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Random;

public class Fireworks {

    public static void spawnFirework(User user) {
        final Firework firework = (Firework) user.getPlayer().getLocation().getWorld()
                .spawnEntity(user.getPlayer().getLocation().add(0.5, 0.5, 0.5), EntityType.FIREWORK);
        FireworkMeta meta = firework.getFireworkMeta();
        Random r = new Random();
        int rt = r.nextInt(4) + 1;
        Type type = Type.BALL;
        if (rt == 1)
            type = Type.BALL;
        if (rt == 2)
            type = Type.BALL_LARGE;
        if (rt == 3)
            type = Type.BURST;
        if (rt == 4)
            type = Type.CREEPER;
        if (rt == 5)
            type = Type.STAR;
        int r1i = r.nextInt(17) + 1;
        int r2i = r.nextInt(17) + 1;
        Color c1 = getColor(r1i);
        Color c2 = getColor(r2i);
        FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withFade(c2).with(type)
                .trail(r.nextBoolean()).build();
        meta.addEffect(effect);
        int rp = r.nextInt(2) + 1;
        meta.setPower(rp);
        firework.setFireworkMeta(meta);
        Bukkit.getScheduler().runTaskLater(TNTTag.getInstance(), firework::detonate, 2);
    }

    private static Color getColor(int i) {
        Color c = null;
        if (i == 1) {
            c = Color.AQUA;
        }
        if (i == 2) {
            c = Color.BLACK;
        }
        if (i == 3) {
            c = Color.BLUE;
        }
        if (i == 4) {
            c = Color.FUCHSIA;
        }
        if (i == 5) {
            c = Color.GRAY;
        }
        if (i == 6) {
            c = Color.GREEN;
        }
        if (i == 7) {
            c = Color.LIME;
        }
        if (i == 8) {
            c = Color.MAROON;
        }
        if (i == 9) {
            c = Color.NAVY;
        }
        if (i == 10) {
            c = Color.OLIVE;
        }
        if (i == 11) {
            c = Color.ORANGE;
        }
        if (i == 12) {
            c = Color.PURPLE;
        }
        if (i == 13) {
            c = Color.RED;
        }
        if (i == 14) {
            c = Color.SILVER;
        }
        if (i == 15) {
            c = Color.TEAL;
        }
        if (i == 16) {
            c = Color.WHITE;
        }
        if (i == 17) {
            c = Color.YELLOW;
        }

        return c;
    }

}
