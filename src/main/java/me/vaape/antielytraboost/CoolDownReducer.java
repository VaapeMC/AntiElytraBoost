package me.vaape.antielytraboost;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class CoolDownReducer extends BukkitRunnable {
    @Override
    public void run() {
        HashMap<UUID, Integer> cooldowns = AntiElytraBoost.plugin.boostCooldown;
        for (UUID uuid : cooldowns.keySet()) {
            int value = cooldowns.get(uuid);

            if (value > 0) {
                cooldowns.put(uuid, value - 1);
                continue;
            }

            if (value == 0) {
                Player p = Bukkit.getPlayer(uuid);
                if (p != null) {
                    p.sendMessage(ChatColor.GREEN + "Elytra boost refreshed");
                }

                cooldowns.remove(uuid);
            }
        }
    }
}
