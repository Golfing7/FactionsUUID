package com.massivecraft.factions.cmd;

import com.drtshock.playervaults.PlayerVaults;
import com.drtshock.playervaults.vaultmanagement.VaultManager;
import com.drtshock.playervaults.vaultmanagement.VaultOperations;
import com.drtshock.playervaults.vaultmanagement.VaultViewInfo;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class CmdVault extends FCommand {

    public CmdVault() {
        this.aliases.add("vault");
        this.aliases.add("chest");

        this.requirements = new CommandRequirements.Builder(Permission.VAULT)
                .memberOnly()
                .noDisableOnLock()
                .brigadier(VaultBrigadier.class)
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        /*
             /f vault <number>
         */

        Player player = context.player;

        if(context.faction.isWilderness())return;

        Inventory inventory = context.faction.getChest();

        player.openInventory(inventory);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_VAULT_DESCRIPTION;
    }

    protected class VaultBrigadier implements BrigadierProvider {
        @Override
        public ArgumentBuilder<Object, ?> get(ArgumentBuilder<Object, ?> parent) {
            return parent.then(RequiredArgumentBuilder.argument("number", IntegerArgumentType.integer(0, 99)));
        }
    }

}
