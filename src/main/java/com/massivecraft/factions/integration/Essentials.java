package com.massivecraft.factions.integration;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.Teleport;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.massivecraft.factions.*;
import com.massivecraft.factions.iface.EconomyParticipator;
import com.massivecraft.factions.listeners.EssentialsListener;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.struct.Permission;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class Essentials {

    private static IEssentials essentials;

    public static void setup(Plugin ess) {
        essentials = (IEssentials) ess;
        FactionsPlugin plugin = FactionsPlugin.getInstance();
        plugin.getLogger().info("Found and connected to Essentials");
        if (plugin.conf().factions().other().isDeleteEssentialsHomes()) {
            plugin.getLogger().info("Based on main.conf will delete Essentials player homes in their old faction when they leave");
            plugin.getServer().getPluginManager().registerEvents(new EssentialsListener(essentials), plugin);
        }
        if (plugin.conf().factions().homes().isTeleportCommandEssentialsIntegration()) {
            plugin.getLogger().info("Using Essentials for teleportation");
        }
    }

    // return false if feature is disabled or Essentials isn't available
    public static boolean handleTeleport(Player player, Location loc) {
        if (!FactionsPlugin.getInstance().conf().factions().homes().isTeleportCommandEssentialsIntegration() || essentials == null) {
            return false;
        }

        Teleport teleport = essentials.getUser(player).getTeleport();
        Trade trade = new Trade(FactionsPlugin.getInstance().conf().economy().getCostHome(), essentials);
        try {
            teleport.teleport(loc, trade);
        } catch (Exception e) {
            player.sendMessage(ChatColor.RED.toString() + e.getMessage());
        }
        return true;
    }

    public static boolean handleDefaultTeleport(Player player, Location loc) {
        if (!FactionsPlugin.getInstance().conf().factions().homes().isTeleportCommandEssentialsIntegration() || essentials == null) {
            return true;
        }

        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);

        if(fPlayer.isAdminBypassing() || Permission.BYPASS_REMOVAL.has(fPlayer.getPlayer()))return true;

        Faction factionAt = Board.getInstance().getFactionAt(new FLocation(loc));
        Relation to = fPlayer.getRelationTo(factionAt);

        boolean returnb = true;

        if((to.isNeutral() || to.isEnemy()) && !(factionAt.isWarZone() || factionAt.isSafeZone() || factionAt.isWilderness())){
            User user = essentials.getUser(player);

            //Clone list so we can modify the homes
            for(String home : new ArrayList<>(user.getHomes())){
                try {
                    Location homeLoc = user.getHome(home);

                    Faction facAtHome = Board.getInstance().getFactionAt(new FLocation(homeLoc));

                    Relation relationTo = fPlayer.getRelationTo(facAtHome);
                    if(relationTo.isEnemy() && !(facAtHome.isWarZone() || facAtHome.isSafeZone() || facAtHome.isWilderness())){
                        user.delHome(home);

                        player.sendMessage(ChatColor.RED + "One of your homes was removed due to it being in an enemy claim!");
                        returnb = false;
                    }
                } catch (Exception e) {
                    player.sendMessage(ChatColor.RED.toString() + e.getMessage());
                }
            }
        }
        return returnb;
    }

    public static boolean isVanished(Player player) {
        return essentials != null && player != null && essentials.getUser(player).isVanished();
    }

    public static boolean isOverBalCap(EconomyParticipator participator, double amount) {
        if (essentials == null) {
            return false;
        }

        return amount > essentials.getSettings().getMaxMoney().doubleValue();
    }

    public static Plugin getEssentials() {
        return essentials;
    }
}
