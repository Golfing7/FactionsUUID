package com.massivecraft.factions.cmd.tnt;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cmd.CmdTNT;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;

public class CmdTNTAdminSet extends FCommand {
    public CmdTNTAdminSet(){
        super();
        this.aliases.add("adminset");
        this.requiredArgs.add("faction");
        this.requiredArgs.add("amount");

        this.requirements = new CommandRequirements.Builder(Permission.TNT_ADMIN).playerOnly().build();
    }

    @Override
    public void perform(CommandContext context) {
        Faction faction = context.argAsFaction(0);
        if(faction == null)return;
        int amount = context.argAsInt(1);
        if(amount < 0)return;

        faction.setTNTBank(amount);

        context.msg(TL.COMMAND_TNT_ADMINSET_USAGE, faction.describeTo(context.fPlayer), amount + "");
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_TNT_ADMINSET_USAGE;
    }
}
