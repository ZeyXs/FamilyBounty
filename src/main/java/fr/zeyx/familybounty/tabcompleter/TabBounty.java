package fr.zeyx.familybounty.tabcompleter;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class TabBounty implements TabCompleter {

    List<String> arg1 = new ArrayList<String>();
    List<String> arg2 = new ArrayList<String>();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        // /BOUNTY <p>
        if (arg1.isEmpty()) {
            for (OfflinePlayer p : Bukkit.getOfflinePlayers()) { arg1.add(p.getName()); }
            arg1.add("help");
            arg1.add("cancel");
            arg1.add("forcecancel");

        }
        // /BOUNTY CANCEL/FORCECANCEL <p>
        if (arg2.isEmpty()) { for (OfflinePlayer p : Bukkit.getOfflinePlayers()) { arg2.add(p.getName()); } }

        List<String> playersResult = new ArrayList<String>();
        //SHOW /BOUNTY <p>
        if (args.length == 1) { for (String p : arg1) { if (p.toLowerCase().startsWith(args[0].toLowerCase())) { playersResult.add(p); } }return playersResult;
        //SHOW /BOUNTY CANCEL/FORCECANCEL <p>
        } else if (args.length == 2) { for (String p : arg2) { if (p.toLowerCase().startsWith(args[1].toLowerCase())) { playersResult.add(p); } }return playersResult;
        }

        //SECOND ARGUMENTS -> FORCECANCEL

        return null;
    }
}
