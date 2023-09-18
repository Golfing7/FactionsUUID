package com.massivecraft.factions.integration;

import net.minelink.ctplus.CombatTagPlus;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class CombatTagPlusIntegration {

    private static Plugin plugin;

    public static boolean playerIsInCombat(UUID player){
        if(plugin == null) plugin = Bukkit.getPluginManager().getPlugin("CombatTagPlus");
        if(plugin == null || !plugin.isEnabled())return false;
        return ((CombatTagPlus) plugin).getTagManager().isTagged(player);
    }
}
