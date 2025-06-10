package com.massivecraft.factions.cmd.tnt;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;

public class CmdTNTAdminAdd extends FCommand {
    public CmdTNTAdminAdd(){
        super();
        this.aliases.add("adminadd");
        this.requiredArgs.add("faction");
        this.requiredArgs.add("amount");

        this.requirements = new CommandRequirements.Builder(Permission.TNT_ADMIN).build();
    }

    @Override
    public void perform(CommandContext context) {
        Faction faction = context.argAsFaction(0);
        if(faction == null)return;
        int amount = context.argAsInt(1);

        faction.setTNTBank(Math.max(faction.getTNTBank() + amount, 0));

        context.msg(TL.COMMAND_TNT_ADMINADD_ADDSUCCESSFUL, amount + "", context.fPlayer != null ? faction.describeTo(context.fPlayer) : faction.getTag());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_TNT_ADMINADD_USAGE;
    }
}
