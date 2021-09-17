package fr.zeyx.familybounty.commands;

import fr.zeyx.familybounty.FamilyBounty;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class CommandBounty implements CommandExecutor, Listener {

    FamilyBounty main;
    public CommandBounty(FamilyBounty familyBounty) {
        this.main = familyBounty;
    }

    //MAIN COMMAND
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length > 0) {
                //HELP
                if (args[0].equals("help")) {
                    player.sendMessage(" ");
                    player.sendMessage("          §6§lBOUNTY §r§e(/bounty help)");
                    player.sendMessage(" ");
                    player.sendMessage("§f- §e/bounty <joueur> §8» §7Mettre un bounty sur quelqu'un.");
                    player.sendMessage("§f- §e/bounty cancel <joueur> §8» §7Annuler un bounty.");
                    player.sendMessage("§f- §e/bounty help §8» §7Affiche l'aide aux commandes.");
                    player.sendMessage("§f- §e/bounty admin §8» §7Affiche les commandes administrateurs.");
                    player.sendMessage(" ");
                    return false;
                }
                if (args[0].equals("admin")) {
                    player.sendMessage(" ");
                    player.sendMessage("          §6§lBOUNTY §r§e(§cADMIN§e)");
                    player.sendMessage(" ");
                    player.sendMessage("§f- §e/bounty forcecancel <joueur> §8» §7Annuler un bounty.");
                    player.sendMessage(" ");
                    return false;
                }
                //DEBUG
                if (args[0].equals("debug")) {
                    main.getConfig().set("bountyeur." + player.getName(), null);
                }
                //FORCECANCEL
                if (args[0].equals("forcecancel")) {
                    if (!player.hasPermission("bounty.admin.forcecancel")) {
                        player.sendMessage("§cVous n'avez pas la permission d'éxécuter cette commande.");
                        return false;
                    }
                    if (args.length == 2) {
                        OfflinePlayer target = (OfflinePlayer) Bukkit.getOfflinePlayer(args[1]);
                        if (!target.hasPlayedBefore()) {
                            player.sendMessage("§8[§6Bounty§8] §7Ce joueur n'existe pas.");
                            return false;
                        }
                        if (main.bounty.containsKey(target.getName())) {
                            main.bounty.remove(target.getName());
                            main.getConfig().set("bountyeur." + player.getName(), null);
                            main.getConfig().set("bounty." + target.getName(), null);
                            main.saveConfig();
                            if (target.getPlayer() != null) {
                                target.getPlayer().setPlayerListName(null);
                            }
                            Bukkit.broadcastMessage("§6§lBOUNTY §8• §r§a" + target.getName() + " §7n'est désormais plus une cible.");
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 50, 1);
                            }
                        } else {
                            player.sendMessage("§8[§6Bounty§8] §7Ce joueur n'a pas de bounty actif sur lui.");
                        }
                    } else {
                        player.sendMessage("§8[§6Bounty§8] §7Syntaxe : §e/bounty forcecancel <joueur>");
                    }
                    return false;
                }
                //CANCEL
                if (args[0].equals("cancel")) {
                    if (args.length == 2) {
                        OfflinePlayer target = (OfflinePlayer) Bukkit.getOfflinePlayer(args[1]);
                        if (!target.hasPlayedBefore()) {
                            player.sendMessage("§8[§6Bounty§8] §7Ce joueur n'existe pas.");
                            return false;
                        }
                        if (main.bounty.containsKey(target.getName())) {
                            if(!main.bountyeur.containsKey(player.getName())) {
                                player.sendMessage("§8[§6Bounty§8] §7Vous n'êtes pas le joueur qui a mis un bounty sur la tête de §a" + target.getName());
                                return false;
                            }
                            ItemStack cancel;
                            int demiReward = main.bounty.get(target.getName()) /  2;
                            Bukkit.broadcastMessage("§6§lBOUNTY §8• §r§e" + player.getName() + " §7a annulé le bounty de §a" + target.getName());
                            if (main.bountyeur.get(player.getName())) {
                                cancel = new ItemStack(Material.DIAMOND);
                                player.sendMessage("§3✔ §750% de la prime commanditée vous a été restitué. (§b" + demiReward + " diamants§7)");
                            } else {
                                cancel = new ItemStack(Material.DIAMOND_BLOCK);
                                player.sendMessage("§3✔ §750% de la prime commanditée vous a été restitué. (§b" + demiReward + " blocs de diamants§7)");
                            }
                            cancel.setAmount(demiReward);
                            player.getInventory().addItem(cancel);
                            main.bounty.remove(target.getName());
                            main.bountyeur.remove(player.getName());
                            main.getConfig().set("bountyeur." + player.getName(), null);
                            main.getConfig().set("bounty." + target.getName(), null);
                            main.saveConfig();
                            if (target.getPlayer() != null) {
                                target.getPlayer().setPlayerListName(null);
                            }
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 50, 1);
                            }
                        } else {
                            player.sendMessage("§8[§6Bounty§8] §7Ce joueur n'a pas de bounty actif sur lui.");
                        }
                    } else {
                        player.sendMessage("§8[§6Bounty§8] §7Syntaxe : §e/bounty cancel <joueur>");
                    }
                    return false;
                }
                //OPEN GUI - BOUNTY
                main.price = 0;
                OfflinePlayer target = (OfflinePlayer) Bukkit.getOfflinePlayer(args[0]);
                if (main.bounty.containsKey(target.getName())) {
                    player.sendMessage("§8[§6Bounty§8] §7Ce joueur a déjà un bounty actif sur lui");
                    return false;
                }
                if (target == player) {
                    player.sendMessage("§8[§6Bounty§8] §7Vous ne pouvez pas mettre un bounty sur vous-même.");
                    return false;
                }
                if (main.bountyeur.containsKey(player.getName())) {
                    player.sendMessage("§8[§6Bounty§8] §7Vous avez déjà mis une prime sur un joueur. Attendez que celle-ci se termine.");
                    return false;
                }
                if(!target.hasPlayedBefore()) {
                    player.sendMessage("§8[§6Bounty§8] §7Ce joueur n'existe pas.");
                    return false;
                }
                main.tar.put(player.getUniqueId(), target.getUniqueId());
                Inventory guiDiamond = Bukkit.createInventory(player, 27, "§8Choisir le prix du bounty :");
                //buttons
                ItemStack info = createItem(Material.KNOWLEDGE_BOOK, 1, "§3▶ §b" + main.price + " diamants", Arrays.asList(" ", "§8• §7Ces diamants seront", "§7directement prélevés", "§7de votre inventaire.", " ", "§4✘ §c§lVALIDER"));
                ItemStack removeFive = createItem(Material.RED_STAINED_GLASS_PANE, 5, "§c[-] Enlever 5", Collections.singletonList("§4← " + main.price));
                ItemStack removeOne = createItem(Material.ORANGE_STAINED_GLASS_PANE, 1, "§c[-] Enlever 1", Collections.singletonList("§4← " + main.price));ItemStack addOne = createItem(Material.LIME_STAINED_GLASS_PANE, 1, "§a[+] Ajouter 1", Collections.singletonList("§2" + main.price + " →"));
                ItemStack addFive = createItem(Material.GREEN_STAINED_GLASS_PANE, 5, "§a[+] Ajouter 5", Collections.singletonList("§2" + main.price + " →"));
                ItemStack type = createItem(Material.DIAMOND, 1, "§7Mise à prix en :", Arrays.asList("§a▶ §bDiamant", "§8▶ Bloc de diamant"));
                guiDiamond.setItem(13, info); guiDiamond.setItem(4, type);
                guiDiamond.setItem(16, addFive); guiDiamond.setItem(15, addOne);
                guiDiamond.setItem(10, removeFive); guiDiamond.setItem(11, removeOne);
                //decorations
                ItemStack black = createItem(Material.BLACK_STAINED_GLASS_PANE, 1, " ", null);
                ItemStack gray = createItem(Material.GRAY_STAINED_GLASS_PANE, 1, " ", null);
                guiDiamond.setItem(0, black); guiDiamond.setItem(8, black); guiDiamond.setItem(9, black); guiDiamond.setItem(17, black); guiDiamond.setItem(18, black); guiDiamond.setItem(26, black);
                for (int i = 0; i < 27; i++) {
                    if (guiDiamond.getItem(i) == null) {
                        guiDiamond.setItem(i, gray);
                    }
                }
                player.openInventory(guiDiamond);
                player.playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 50, 1);
            } else {
                player.sendMessage("§8[§6Bounty§8] §7Merci de renseigner un argument valide (§e/bounty help§7)");
            }

        }

        else {
            sender.sendMessage("§cVous devez être un joueur pour éxécuter cette commande.");
        }
        return true;
    }

    //ITEM BUILDER
    private ItemStack createItem(Material mat, int amount, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat, amount);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }

    //BUTTON UPDATER
    private void updateButton(Inventory inv) {
        ItemStack removeFive = createItem(Material.RED_STAINED_GLASS_PANE, 5, "§c[-] Enlever 5", Collections.singletonList("§4← " + main.price));
        ItemStack removeOne = createItem(Material.ORANGE_STAINED_GLASS_PANE, 1, "§c[-] Enlever 1", Collections.singletonList("§4← " + main.price));
        ItemStack addOne = createItem(Material.LIME_STAINED_GLASS_PANE, 1, "§a[+] Ajouter 1", Collections.singletonList("§2" + main.price + " →"));
        ItemStack addFive = createItem(Material.GREEN_STAINED_GLASS_PANE, 5, "§a[+] Ajouter 5", Collections.singletonList("§2" + main.price + " →"));
        inv.setItem(16, addFive); inv.setItem(15, addOne);
        inv.setItem(10, removeFive); inv.setItem(11, removeOne);
    }

    //INFO UPDATER
    private void updateInfo(Inventory inv, boolean isValid, boolean diamondType) {
        if (diamondType) {
            ItemStack check;
            if (isValid) {
                check = createItem(Material.KNOWLEDGE_BOOK, 1, "§3▶ §b" + main.price + " diamants", Arrays.asList(" ", "§8• §7Ces diamants seront", "§7directement prélevés", "§7de votre inventaire.", " ", "§2✔ §a§lVALIDER"));
            } else {
                check = createItem(Material.KNOWLEDGE_BOOK, 1, "§3▶ §b" + main.price + " diamants", Arrays.asList(" ", "§8• §7Ces diamants seront", "§7directement prélevés", "§7de votre inventaire.", " ", "§4✘ §c§lVALIDER"));
            }
            inv.setItem(13, check);
        } else {
            ItemStack check;
            if (isValid) {
                check = createItem(Material.KNOWLEDGE_BOOK, 1, "§3▶ §b" + main.price + " blocs de diamants", Arrays.asList(" ", "§8• §7Ces blocs seront", "§7directement prélevés", "§7de votre inventaire.", " ", "§2✔ §a§lVALIDER"));
            } else {
                check = createItem(Material.KNOWLEDGE_BOOK, 1, "§3▶ §b" + main.price + " blocs de diamants", Arrays.asList(" ", "§8• §7Ces blocs seront", "§7directement prélevés", "§7de votre inventaire.", " ", "§4✘ §c§lVALIDER"));
            }
            inv.setItem(13, check);
        }
    }

    //GUI ACTIONS
    @EventHandler
    public void onClick(InventoryClickEvent e){
        Inventory inv = e.getInventory();
        Player player = (Player) e.getWhoClicked();
        ItemStack current = e.getCurrentItem();
        if (current == null) { return; }
        if(e.getView().getTitle().equalsIgnoreCase("§8Choisir le prix du bounty :")) {
            e.setCancelled(true);
            if(current.getType() == Material.KNOWLEDGE_BOOK) {
                if (main.price == 0) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 50, 0.8f);
                } else {
                    //EXECUTE
                    int count = 0;
                    PlayerInventory inventory = player.getInventory();
                    if (main.diamondType) {
                        for (ItemStack diamond : inventory.all(Material.DIAMOND).values()) {
                            if (diamond != null && diamond.getType() == Material.DIAMOND) {
                                count = count + diamond.getAmount();
                            }
                        }
                    } else {
                        for (ItemStack diamondb : inventory.all(Material.DIAMOND_BLOCK).values()) {
                            if (diamondb != null && diamondb.getType() == Material.DIAMOND_BLOCK) {
                                count = count + diamondb.getAmount();
                            }
                        }
                    }
                    if (count < main.price) {
                        if (main.diamondType) {
                            player.sendMessage("§8[§6Bounty§8] §7Vous n'avez pas assez de diamants. (§b" + main.price + " diamants requis§7)");
                        } else {
                            player.sendMessage("§8[§6Bounty§8] §7Vous n'avez pas assez de blocs de diamants. (§b" + main.price + " blocs requis§7)");
                        }
                        player.closeInventory();
                    } else {
                        OfflinePlayer target = Bukkit.getOfflinePlayer(main.tar.get(player.getUniqueId()));
                        Inventory currentInv = player.getPlayer().getInventory();
                        main.bountyeur.put(player.getName(), main.diamondType);
                        main.bounty.put(target.getName(), main.price);
                        if (main.diamondType) {
                            Bukkit.broadcastMessage(" ");
                            Bukkit.broadcastMessage("§6§lBOUNTY §8• §r§a" + player.getName() + " §7a mis un bounty sur la tête de §c" + target.getName() + " §b(" + main.bounty.get(target.getName())  + " diamants)");
                            Bukkit.broadcastMessage(" ");
                            currentInv.removeItem(new ItemStack(Material.DIAMOND, main.price));
                        } else {
                            Bukkit.broadcastMessage(" ");
                            Bukkit.broadcastMessage("§6§lBOUNTY §8• §r§a" + player.getName() + " §7a mis un bounty sur la tête de §c" + target.getName() + " §b(" + main.bounty.get(target.getName())  + " blocs de diamants)");
                            Bukkit.broadcastMessage(" ");
                            currentInv.removeItem(new ItemStack(Material.DIAMOND_BLOCK, main.price));
                        }
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            p.playSound(p.getLocation(), Sound.EVENT_RAID_HORN, 50, 1);
                        }
                        if (target.getPlayer() != null) {
                            target.getPlayer().setPlayerListName(target.getName() + " §6§lBOUNTY");
                        }
                        main.createBossbar(target);
                        player.closeInventory();
                    }
                }
            //ADD 1
            } else if (current.getType() == Material.LIME_STAINED_GLASS_PANE) {
                main.price++;
                updateInfo(inv, true, main.diamondType);
                updateButton(inv);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 50, 2);
            //ADD 5
            } else if (current.getType() == Material.GREEN_STAINED_GLASS_PANE) {
                main.price = main.price + 5;
                updateInfo(inv, true, main.diamondType);
                updateButton(inv);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 50, 2);
            // REMOVE 1
            } else if (current.getType() == Material.ORANGE_STAINED_GLASS_PANE) {
                if (main.price == 0) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 50, 2);
                } else {
                    main.price--;
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 50, 2);
                    if (main.price == 0) {
                        updateInfo(inv, false, main.diamondType);
                    } else {
                        updateInfo(inv, true, main.diamondType);
                    }
                    updateButton(inv);
                }
            //REMOVE 5
            } else if (current.getType() == Material.RED_STAINED_GLASS_PANE) {
                if (main.price == 0) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 50, 2);
                } else {
                    main.price = main.price - 5;
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 50, 2);
                    if (main.price < 0) {
                        main.price = 0;
                        updateInfo(inv, false, true);
                    } else {
                        updateInfo(inv, false, false);
                    }
                    updateButton(inv);
                }
            //CHANGE TYPE
            } else if (current.getType() == Material.DIAMOND){
                main.diamondType = false;
                main.price = 0;
                updateButton(inv);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 50, 1);
                ItemStack newType = createItem(Material.DIAMOND_BLOCK, 1, "§7Mise à prix en :", Arrays.asList("§8▶ Diamant", "§a▶ §bBloc de diamant"));
                ItemStack check = createItem(Material.KNOWLEDGE_BOOK, 1, "§3▶ §b" + main.price + " blocs de diamants", Arrays.asList(" ", "§8• §7Ces blocs seront", "§7directement prélevés", "§7de votre inventaire.", " ", "§4✘ §c§lVALIDER"));
                inv.setItem(13, check); inv.setItem(4, newType);
            } else if (current.getType() == Material.DIAMOND_BLOCK){
                main.diamondType = true;
                main.price = 0;
                updateButton(inv);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 50, 1);
                ItemStack newType = createItem(Material.DIAMOND, 1, "§7Mise à prix en :", Arrays.asList("§a▶ §bDiamant", "§8▶ Bloc de diamant"));
                ItemStack check = createItem(Material.KNOWLEDGE_BOOK, 1, "§3▶ §b" + main.price + " diamants", Arrays.asList(" ", "§8• §7Ces diamants seront", "§7directement prélevés", "§7de votre inventaire.", " ", "§4✘ §c§lVALIDER"));
                inv.setItem(13, check); inv.setItem(4, newType);
            }
        }
    }

    //CLOSE INVENTORY
    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if(e.getView().getTitle().equalsIgnoreCase("§8Choisir le prix du bounty :")) {
            main.diamondType = true;
        }
    }
}