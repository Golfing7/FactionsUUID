package com.massivecraft.factions.cmd.alts;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;

public class CmdAltOpen extends FCommand {

    public CmdAltOpen(){
        super();
        this.aliases.add("open");

        this.requirements = new CommandRequirements.Builder(Permission.ALTS).withAction(PermissibleAction.INVITE).memberOnly().build();
    }

    @Override
    public void perform(CommandContext context) {
        Faction faction = context.faction;

        if(!faction.getAltJoinString().equals("-1")){
            context.msg(TL.COMMAND_ALT_ALREADYOPEN, faction.describeTo(context.fPlayer), faction.getAltJoinString());
            return;
        }

        String string = faction.setAltOpen();

        context.msg(TL.COMMAND_ALT_OPEN, faction.describeTo(context.fPlayer), string);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_ALT_OPENUSAGE;
    }
}
