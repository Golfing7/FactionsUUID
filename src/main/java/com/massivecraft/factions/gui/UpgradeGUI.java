package com.massivecraft.factions.gui;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.data.MemoryFaction;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.util.TextUtil;
import com.massivecraft.factions.util.UpgradeType;
import com.massivecraft.factions.util.material.MaterialDb;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class UpgradeGUI extends GUI<UpgradeType>{
    private static final Map<UpgradeType, SimpleItem> items;

    static {
        items = new LinkedHashMap<>();

        items.put(UpgradeType.TNT, FactionsPlugin.getInstance().conf().upgrades().tnt().item().getItem());

        items.put(UpgradeType.CHEST, FactionsPlugin.getInstance().conf().upgrades().chest().item().getItem());

        items.put(UpgradeType.SPAWNER_BOOST, FactionsPlugin.getInstance().conf().upgrades().spawnerBoost().item().getItem());

        items.put(UpgradeType.CROP_BOOST, FactionsPlugin.getInstance().conf().upgrades().cropBoost().item().getItem());
    }

    public UpgradeGUI(FPlayer user) {
        super(user, FactionsPlugin.getInstance().conf().upgrades().gui().getRows());
        build();
    }

    @Override
    protected String getName() {
        return TextUtil.parseColor(FactionsPlugin.getInstance().conf().upgrades().guiTitle());
    }

    @Override
    protected String parse(String toParse, UpgradeType upgradeType) {
        String toReturn = toParse;

        switch(upgradeType){
            case TNT:
                for(int z = 1; z <= FactionsPlugin.getInstance().conf().upgrades().tnt().getLevels().size(); z++){
                    toReturn = toReturn.replace("{AMOUNT_" + z + "}", "" + FactionsPlugin.getInstance().conf().upgrades().tnt().getNumber(z));

                    toReturn = toReturn.replace("{PRICE_" + z + "}", "" + FactionsPlugin.getInstance().conf().upgrades().tnt().getCost(z));
                }

                toReturn = toReturn.replace("{LEVEL}", user.getFaction().getUpgrade(upgradeType) + "");
                break;
            case CHEST:
                for(int z = 1; z <= FactionsPlugin.getInstance().conf().upgrades().chest().getLevels().size(); z++){
                    toReturn = toReturn.replace("{AMOUNT_" + z + "}", "" + FactionsPlugin.getInstance().conf().upgrades().chest().getNumber(z));

                    toReturn = toReturn.replace("{PRICE_" + z + "}", "" + FactionsPlugin.getInstance().conf().upgrades().chest().getCost(z));
                }

                toReturn = toReturn.replace("{LEVEL}", user.getFaction().getUpgrade(upgradeType) + "");
                break;
            case SPAWNER_BOOST:
                for(int z = 1; z <= FactionsPlugin.getInstance().conf().upgrades().spawnerBoost().getLevels().size(); z++){
                    toReturn = toReturn.replace("{AMOUNT_" + z + "}", "" + FactionsPlugin.getInstance().conf().upgrades().spawnerBoost().getNumber(z));

                    toReturn = toReturn.replace("{PRICE_" + z + "}", "" + FactionsPlugin.getInstance().conf().upgrades().spawnerBoost().getCost(z));
                }

                toReturn = toReturn.replace("{LEVEL}", user.getFaction().getUpgrade(upgradeType) + "");
                break;
            case CROP_BOOST:
                for(int z = 1; z <= FactionsPlugin.getInstance().conf().upgrades().cropBoost().getLevels().size(); z++){
                    toReturn = toReturn.replace("{AMOUNT_" + z + "}", "" + FactionsPlugin.getInstance().conf().upgrades().cropBoost().getNumber(z));

                    toReturn = toReturn.replace("{PRICE_" + z + "}", "" + FactionsPlugin.getInstance().conf().upgrades().cropBoost().getCost(z));
                }

                toReturn = toReturn.replace("{LEVEL}", user.getFaction().getUpgrade(upgradeType) + "");
                break;
        }
        return toReturn;
    }

    @Override
    protected void onClick(UpgradeType action, ClickType clickType) {
        if(user.getFaction().isWilderness()){
            user.msg(TextUtil.parseColor("&cYou cannot use upgrades while in wilderness!"));
            user.getPlayer().closeInventory();
            return;
        }

        if(((MemoryFaction) user.getFaction()).doUpgrade(action, user)){
            ((MemoryFaction) user.getFaction()).commitUpgrade(action);

            user.getPlayer().closeInventory();
        }
    }

    @Override
    protected Map<Integer, UpgradeType> createSlotMap() {
        Map<Integer, UpgradeType> map = new HashMap<>();
        if(FactionsPlugin.getInstance().conf().upgrades().tnt().isEnabled())
            map.put(FactionsPlugin.getInstance().conf().upgrades().tnt().item().getSlot(), UpgradeType.TNT);
        if(FactionsPlugin.getInstance().conf().upgrades().chest().isEnabled())
            map.put(FactionsPlugin.getInstance().conf().upgrades().chest().item().getSlot(), UpgradeType.CHEST);
        if(FactionsPlugin.getInstance().conf().upgrades().spawnerBoost().isEnabled())
            map.put(FactionsPlugin.getInstance().conf().upgrades().spawnerBoost().item().getSlot(), UpgradeType.SPAWNER_BOOST);
        if(FactionsPlugin.getInstance().conf().upgrades().cropBoost().isEnabled())
            map.put(FactionsPlugin.getInstance().conf().upgrades().cropBoost().item().getSlot(), UpgradeType.CROP_BOOST);
        return map;
    }

    @Override
    protected SimpleItem getItem(UpgradeType upgradeType) {
        return new SimpleItem(items.get(upgradeType));
    }

    @Override
    protected Map<Integer, SimpleItem> createDummyItems() {
        Map<Integer, SimpleItem> map = new HashMap<>();
        for(int z = 0; z < this.size; z++){
            map.put(z, FactionsPlugin.getInstance().conf().upgrades().filler().getItem());
        }
        return map;
    }
}
