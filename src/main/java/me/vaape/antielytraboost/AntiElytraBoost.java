package me.vaape.antielytraboost;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;

public class AntiElytraBoost extends JavaPlugin implements Listener {

    public static AntiElytraBoost plugin;

    private final HashMap<UUID, Integer> boostCooldown = new HashMap<UUID, Integer>(); //Cooldown for players who
    // have shot
    // themselves or used firework
    private final HashMap<UUID, BukkitRunnable> boostCooldownTask = new HashMap<UUID, BukkitRunnable>();

    public void onEnable() {
        plugin = this;
        getLogger().info(ChatColor.GREEN + "AntiElytraBoost has been enabled!");
        getServer().getPluginManager().registerEvents(this, this);
    }

    public void onDisable() {
        plugin = null;
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        Entity defender = event.getEntity();
        Entity attacker = event.getDamager();

        if (attacker instanceof Arrow && defender instanceof Player) {
            Arrow arrow = (Arrow) attacker;
            Player player = (Player) defender;
            UUID UUID = player.getUniqueId();

            if (player.isGliding()) { //If flying an elytra
                if (arrow.getShooter() == player) { //If shooting themselves
                    if (boostCooldown.containsKey(UUID)) { //If on cooldown
                        event.setCancelled(true);
                        player.sendMessage(ChatColor.RED + "You cannot elytra boost for another " + boostCooldown.get(UUID) + " seconds.");
                    } else {
                        boostCooldown.put(UUID, 180);
                        boostCooldownTask.put(UUID, new BukkitRunnable() {

                            @Override
                            public void run() {
                                boostCooldown.put(UUID, boostCooldown.get(UUID) - 1); //Lower cooldown by 1 second
                                if (boostCooldown.get(UUID) == 0) {
                                    boostCooldown.remove(UUID);
                                    boostCooldownTask.remove(UUID);
                                    Bukkit.getPlayer(UUID).sendMessage(ChatColor.GREEN + "Elytra boost refreshed");
                                    cancel();
                                }
                            }
                        });

                        boostCooldownTask.get(UUID).runTaskTimer(plugin, 20, 20);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onFireworkLaunch(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            UUID UUID = player.getUniqueId();
            if (player.isGliding()) { //If flying an elytra
                if (player.getInventory().getItemInMainHand().getType() == Material.FIREWORK_ROCKET ||
                        player.getInventory().getItemInOffHand().getType() == Material.FIREWORK_ROCKET) { //If
                    // holding rocket

                    if (boostCooldown.containsKey(UUID)) { //If on cooldown
                        event.setCancelled(true);
                        player.sendMessage(ChatColor.RED + "You cannot elytra boost for another " + boostCooldown.get(UUID) + " seconds.");
                    } else {
                        boostCooldown.put(UUID, 180);
                        boostCooldownTask.put(UUID, new BukkitRunnable() {

                            @Override
                            public void run() {
                                boostCooldown.put(UUID, boostCooldown.get(UUID) - 1); //Lower cooldown by 1 second
                                if (boostCooldown.get(UUID) == 0) {
                                    boostCooldown.remove(UUID);
                                    boostCooldownTask.remove(UUID);
                                    Bukkit.getPlayer(UUID).sendMessage(ChatColor.GREEN + "Elytra boost refreshed");
                                    cancel();
                                }
                            }
                        });

                        boostCooldownTask.get(UUID).runTaskTimer(plugin, 20, 20);
                    }
                }
            }
        }
    }
}
