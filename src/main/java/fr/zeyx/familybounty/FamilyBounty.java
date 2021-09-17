package fr.zeyx.familybounty;

import fr.zeyx.familybounty.commands.CommandBounty;
import fr.zeyx.familybounty.events.DeathBounty;
import fr.zeyx.familybounty.events.JoinBounty;
import fr.zeyx.familybounty.tabcompleter.TabBounty;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class FamilyBounty extends JavaPlugin {

    public Map<String, Integer> bounty = new HashMap<String, Integer>();
    public Map<UUID, UUID> tar = new HashMap<UUID, UUID>();
    public Map<String, Boolean> bountyeur = new HashMap<String, Boolean>();
    public boolean diamondType = true;
    public int price = 0;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        if(getConfig().contains("bounty")) {
            loadBounty();
        }

        getServer().getPluginManager().registerEvents(new CommandBounty(this), this);
        getServer().getPluginManager().registerEvents(new DeathBounty(this), this);
        getServer().getPluginManager().registerEvents(new JoinBounty(this), this);

        getCommand("bounty").setExecutor(new CommandBounty(this));
        getCommand("bounty").setTabCompleter(new TabBounty());

        Bukkit.getConsoleSender().sendMessage("[FamilyBounty] Plugin enabled");
    }

    @Override
    public void onDisable() {
        if (!bounty.isEmpty()) {
            saveBounty();
        }
    }

    public void saveBounty() {
        for(Map.Entry<String, Integer> entry : bounty.entrySet()) {
            getConfig().set("bounty." + entry.getKey().toString(), entry.getValue());
        }
        for(Map.Entry<String, Boolean> entry : bountyeur.entrySet()) {
            getConfig().set("bountyeur." + entry.getKey(), entry.getValue());
        }
        saveConfig();
    }

    public void loadBounty() {
        getConfig().getConfigurationSection("bounty").getKeys(false).forEach(key -> {
            bounty.put(key, getConfig().getInt("bounty." + key));
        });
        getConfig().getConfigurationSection("bountyeur").getKeys(false).forEach(key -> {
            bountyeur.put(key, getConfig().getBoolean("bounty." + key));
        });
    }
}
