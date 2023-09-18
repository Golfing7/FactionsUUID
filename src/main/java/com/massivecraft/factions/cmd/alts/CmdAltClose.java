package com.massivecraft.factions.cmd.alts;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;

public class CmdAltClose extends FCommand {

    public CmdAltClose(){
        super();
        this.aliases.add("close");

        this.requirements = new CommandRequirements.Builder(Permission.ALTS).withAction(PermissibleAction.INVITE).memberOnly().build();
    }

    @Override
    public void perform(CommandContext context) {
        Faction faction = context.faction;

        if(faction.getAltJoinString().equals("-1")){
            context.msg(TL.COMMAND_ALT_ALREADYCLOSED, faction.describeTo(context.fPlayer));
            return;
        }

        faction.setAltClosed();

        context.msg(TL.COMMAND_ALT_CLOSE, faction.describeTo(context.fPlayer));
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_ALT_CLOSEUSAGE;
    }
}
