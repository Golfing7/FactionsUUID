package com.massivecraft.factions.cmd;

import com.massivecraft.factions.gui.UpgradeGUI;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;

public class CmdUpgrade extends FCommand{

    public CmdUpgrade() {
        super();
        this.aliases.add("upgrade");

        this.aliases.add("upgrades");

        this.requirements = new CommandRequirements.Builder(Permission.UPGRADE).memberOnly().playerOnly().build();
    }

    @Override
    public void perform(CommandContext context) {
        UpgradeGUI upgradeGUI = new UpgradeGUI(context.fPlayer);

        upgradeGUI.open();
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_UPGRADE_DESCRIPTION;
    }
}
