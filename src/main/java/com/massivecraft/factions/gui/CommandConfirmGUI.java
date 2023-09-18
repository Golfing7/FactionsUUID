package com.massivecraft.factions.gui;

import com.google.common.collect.Lists;
import com.massivecraft.factions.*;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.event.FactionDisbandEvent;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.scoreboards.FTeamWrapper;
import com.massivecraft.factions.util.TL;
import com.massivecraft.factions.util.TextUtil;
import com.massivecraft.factions.util.material.MaterialDb;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class CommandConfirmGUI extends GUI<Boolean>{
    private SimpleItem yes, no;

    private Faction forFaction;

    private String action;

    private Consumer<Player> clickYes;

    public CommandConfirmGUI(FPlayer user, Faction forFaction, String action, int rows, Consumer<Player> clickYes) {
        super(user, rows);

        this.forFaction = forFaction;
        this.action = action;
        this.clickYes = clickYes;

        yes = SimpleItem.builder().setMaterial(MaterialDb.get("LIME_WOOL")).setData((short) 5).setName("&a&lConfirm " + action).build();
        no = SimpleItem.builder().setMaterial(MaterialDb.get("RED_WOOL")).setData((short) 14).setName("&c&lExit").build();
    }

    @Override
    protected String getName() {
        return TextUtil.parseColor("&c&l" + action + "?");
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
            clickYes.accept(player);

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
