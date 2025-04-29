package com.massivecraft.factions.listeners;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.config.file.MainConfig;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.struct.ChatMode;
import not.savage.chat.spigot.api.events.PlayerChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

public class ShockChatListener extends AbstractListener {

    private final FactionsPlugin plugin;

    public ShockChatListener(FactionsPlugin plugin) {
        this.plugin = plugin;
        Plugin chat = Bukkit.getPluginManager().getPlugin("ShockChat");
        if (chat != null && chat.isEnabled()) {
            Bukkit.getPluginManager().registerEvents(this, this.plugin);
        }
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        if (!plugin.worldUtil().isEnabled(event.getPlayer().getPlayer().getWorld())) {
            return;
        }

        Player talkingPlayer = event.getPlayer().getPlayer();
        String msg = event.getMessage();
        FPlayer me = FPlayers.getInstance().getByPlayer(talkingPlayer);
        ChatMode chat = me.getChatMode();
        MainConfig.Factions.Chat chatConf = FactionsPlugin.getInstance().conf().factions().chat();
        //Is it a MOD chat
        if (chat == ChatMode.MOD) {
            Faction myFaction = me.getFaction();

            String message = String.format(chatConf.getModChatFormat(), ChatColor.stripColor(me.getNameAndTag()), msg);

            //Send to all mods
            for (FPlayer fplayer : FPlayers.getInstance().getOnlinePlayers()) {
                if (myFaction == fplayer.getFaction() && fplayer.getRole().isAtLeast(Role.MODERATOR)) {
                    fplayer.sendMessage(message);
                } else if (fplayer.isSpyingChat() && me != fplayer) {
                    fplayer.sendMessage("[MCspy]: " + message);
                }
            }

            FactionsPlugin.getInstance().log(Level.INFO, ChatColor.stripColor("ModChat " + myFaction.getTag() + ": " + message));

            event.setCancelled(true);
        } else if (chat == ChatMode.FACTION) {
            Faction myFaction = me.getFaction();

            String message = String.format(chatConf.getFactionChatFormat(), me.describeTo(myFaction), msg);
            myFaction.sendMessage(message);

            FactionsPlugin.getInstance().log(Level.INFO, ChatColor.stripColor("FactionChat " + myFaction.getTag() + ": " + message));

            //Send to any players who are spying chat
            for (FPlayer fplayer : FPlayers.getInstance().getOnlinePlayers()) {
                if (fplayer.isSpyingChat() && fplayer.getFaction() != myFaction && me != fplayer) {
                    fplayer.sendMessage("[FCspy] " + myFaction.getTag() + ": " + message);
                }
            }

            event.setCancelled(true);
        } else if (chat == ChatMode.ALLIANCE) {
            Faction myFaction = me.getFaction();

            String message = String.format(chatConf.getAllianceChatFormat(), ChatColor.stripColor(me.getNameAndTag()), msg);

            //Send message to our own faction
            myFaction.sendMessage(message);

            //Send to all our allies
            for (FPlayer fplayer : FPlayers.getInstance().getOnlinePlayers()) {
                if (myFaction.getRelationTo(fplayer) == Relation.ALLY && !fplayer.isIgnoreAllianceChat()) {
                    fplayer.sendMessage(message);
                } else if (fplayer.isSpyingChat() && me != fplayer) {
                    fplayer.sendMessage("[ACspy]: " + message);
                }
            }

            FactionsPlugin.getInstance().log(Level.INFO, ChatColor.stripColor("AllianceChat: " + message));

            event.setCancelled(true);
        } else if (chat == ChatMode.TRUCE) {
            Faction myFaction = me.getFaction();

            String message = String.format(chatConf.getTruceChatFormat(), ChatColor.stripColor(me.getNameAndTag()), msg);

            //Send message to our own faction
            myFaction.sendMessage(message);

            //Send to all our truces
            for (FPlayer fplayer : FPlayers.getInstance().getOnlinePlayers()) {
                if (myFaction.getRelationTo(fplayer) == Relation.TRUCE) {
                    fplayer.sendMessage(message);
                } else if (fplayer.isSpyingChat() && fplayer != me) {
                    fplayer.sendMessage("[TCspy]: " + message);
                }
            }

            FactionsPlugin.getInstance().log(Level.INFO, ChatColor.stripColor("TruceChat: " + message));
            event.setCancelled(true);
        }
    }

}
