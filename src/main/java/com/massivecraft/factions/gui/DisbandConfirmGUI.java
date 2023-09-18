package com.massivecraft.factions.gui;

import com.google.common.collect.Lists;
import com.massivecraft.factions.*;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.event.FactionDisbandEvent;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.scoreboards.FTeamWrapper;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import com.massivecraft.factions.util.TextUtil;
import com.massivecraft.factions.util.material.MaterialDb;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class DisbandConfirmGUI extends GUI<Boolean>{
    private SimpleItem yes, no;
    
    private Faction forFaction;

    public DisbandConfirmGUI(FPlayer user, Faction forFaction, int rows) {
        super(user, rows);

        this.forFaction = forFaction;

        yes = SimpleItem.builder().setMaterial(MaterialDb.get("LIME_WOOL")).setData((short) 5).setName("&a&lConfirm Disband").build();
        no = SimpleItem.builder().setMaterial(MaterialDb.get("RED_WOOL")).setData((short) 14).setName("&c&lExit").build();
    }

    @Override
    protected String getName() {
        return TextUtil.parseColor("&c&lDisband Faction?");
    }

    @Override
    protected String parse(String toParse, Boolean aBoolean) {
        return toParse;
    }

    @Override
    protected void onClick(Boolean action, ClickType clickType) {
        final Player player = user.getPlayer();
        if(!action || user.getFaction() != forFaction){
            new BukkitRunnable(){
                @Override
                public void run(){
                    player.closeInventory();
                }
            }.runTask(FactionsPlugin.getInstance());
        }else{
            if (!user.isAdminBypassing() && !forFaction.hasAccess(user, PermissibleAction.DISBAND)) {
                user.msg(TL.GENERIC_NOPERMISSION.format(PermissibleAction.DISBAND));
                return;
            }

            if (!forFaction.isNormal()) {
                user.msg(TL.COMMAND_DISBAND_IMMUTABLE.toString());
                return;
            }
            if (forFaction.isPermanent()) {
                user.msg(TL.COMMAND_DISBAND_MARKEDPERMANENT.toString());
                return;
            }
            if (!FactionsPlugin.getInstance().getLandRaidControl().canDisbandFaction(forFaction, new CommandContext(player, Lists.newArrayList(), ""))) {
                return;
            }
            
            FactionDisbandEvent disbandEvent = new FactionDisbandEvent(player, forFaction.getId());
            Bukkit.getServer().getPluginManager().callEvent(disbandEvent);
            if (disbandEvent.isCancelled()) {
                return;
            }

            // Send FPlayerLeaveEvent for each player in the faction
            for (FPlayer fplayer : forFaction.getFPlayers()) {
                Bukkit.getServer().getPluginManager().callEvent(new FPlayerLeaveEvent(fplayer, forFaction, FPlayerLeaveEvent.PlayerLeaveReason.DISBAND));
            }

            // Inform all players
            for (FPlayer fplayer : FPlayers.getInstance().getOnlinePlayers()) {
                String who = player == null ? TL.GENERIC_SERVERADMIN.toString() : user.describeTo(fplayer);
                if (fplayer.getFaction() == forFaction) {
                    fplayer.msg(TL.COMMAND_DISBAND_BROADCAST_YOURS, who);
                } else {
                    fplayer.msg(TL.COMMAND_DISBAND_BROADCAST_NOTYOURS, who, forFaction.getTag(fplayer));
                }
            }
            if (FactionsPlugin.getInstance().conf().logging().isFactionDisband()) {
                //TODO: Format this correctly and translate.
                FactionsPlugin.getInstance().log("The faction " + forFaction.getTag() + " (" + forFaction.getId() + ") was disbanded by " + (player == null ? "console command" : user.getName()) + ".");
            }

            if (Econ.shouldBeUsed() && player != null) {
                //Give all the faction's money to the disbander
                double amount = Econ.getBalance(forFaction);
                Econ.transferMoney(user, forFaction, user, amount, false);

                if (amount > 0.0) {
                    String amountString = Econ.moneyString(amount);
                    user.msg(TL.COMMAND_DISBAND_HOLDINGS, amountString);
                    //TODO: Format this correctly and translate
                    FactionsPlugin.getInstance().log(user.getName() + " has been given bank holdings of " + amountString + " from disbanding " + forFaction.getTag() + ".");
                }
            }

            Factions.getInstance().removeFaction(forFaction.getId());
            FTeamWrapper.applyUpdates(forFaction);

            new BukkitRunnable(){
                @Override
                public void run(){
                    if(player != null)player.closeInventory();
                }
            }.runTask(FactionsPlugin.getInstance());
        }
    }

    @Override
    protected Map<Integer, Boolean> createSlotMap() {
        Map<Integer, Boolean> clickMap = new HashMap<>();

        clickMap.put(10, false);
        clickMap.put(16, true);
        return clickMap;
    }

    @Override
    protected SimpleItem getItem(Boolean aBoolean) {
        return aBoolean ? yes : no;
    }

    @Override
    protected Map<Integer, SimpleItem> createDummyItems() {
        Map<Integer, SimpleItem> dummies = new HashMap<>();
        for(int i = 0; i < 27; i++){
            dummies.put(i, SimpleItem.builder().setMaterial(MaterialDb.get("LIGHT_GRAY_STAINED_GLASS_PANE")).setData((short) 7).setName("&7").build());
        }
        return dummies;
    }
}
