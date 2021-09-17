package fr.zeyx.familybounty.events;

import fr.zeyx.familybounty.FamilyBounty;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinBounty implements Listener {

    FamilyBounty main;
    public JoinBounty(FamilyBounty familyBounty) {
        this.main = familyBounty;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (main.bounty.containsKey(player)) {
            player.getPlayer().setPlayerListName(player.getName() + " §6§lBOUNTY");
        } else {
            player.getPlayer().setPlayerListName(null);
        }
    }
}
