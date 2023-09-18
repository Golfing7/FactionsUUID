package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;

public class CmdSeePaypal extends FCommand{
    public CmdSeePaypal(){
        super();
        this.aliases.add("seepaypal");

        this.requiredArgs.add("faction");

        this.requirements = new CommandRequirements.Builder(Permission.SEEPAYPAL).playerOnly().build();
    }

    @Override
    public void perform(CommandContext context) {
        Faction faction = context.argAsFaction(0);

        if(faction == null){
            return;
        }

        if(faction.getPayPal() == null || faction.getPayPal().isEmpty()){
            context.fPlayer.msg(TL.COMMAND_SEEPAYPAL_ISNTSET, faction.describeTo(context.fPlayer));
            return;
        }

        context.fPlayer.msg(TL.COMMAND_SEEPAYPAL_SEE, faction.describeTo(context.fPlayer), faction.getPayPal());
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SEEPAYPAL_DESCRIPTION;
    }
}
