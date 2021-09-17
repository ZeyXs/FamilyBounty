package fr.zeyx.familybounty.events;

import fr.zeyx.familybounty.FamilyBounty;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Map;

public class DeathBounty implements Listener {

    FamilyBounty main;
    public DeathBounty(FamilyBounty familyBounty) {
        this.main = familyBounty;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (!(e.getEntity().getKiller() instanceof Player)) {
            return;
        }
        Player victim = e.getEntity().getPlayer();
        Player attacker = e.getEntity().getKiller();
        if (main.bounty.containsKey(victim.getName())) {
            e.setDeathMessage("");
            victim.setPlayerListName(null);
            Bukkit.broadcastMessage(" ");
            Bukkit.broadcastMessage("§6§lBOUNTY §8• §r§c" + victim.getName() + " §7a été tué par §a" + attacker.getName() + " §7!");
            Bukkit.broadcastMessage(" ");
            e.setDeathMessage(null);
            PlayerInventory inventory = attacker.getInventory();
            if (main.diamondType) {
                inventory.addItem(new ItemStack(Material.DIAMOND, main.bounty.get(victim.getName())));
                attacker.sendMessage("§3✔ §b+" + main.bounty.get(victim.getName()) + " diamant(s)");
            } else {
                inventory.addItem(new ItemStack(Material.DIAMOND_BLOCK, main.bounty.get(victim.getName())));
                attacker.sendMessage("§3✔ §b+" + main.bounty.get(victim.getName()) + " bloc(s) de diamant(s)");
            }
            main.bounty.remove(victim.getName());
            main.bountyeur.remove(attacker.getName());
            main.getConfig().set("bountyeur." + attacker.getName(), null);
            main.getConfig().set("bounty." + victim.getName(), null);
            main.saveConfig();
            for (Player p : Bukkit.getOnlinePlayers()) { p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 50, 2); }
        }
    }
}
