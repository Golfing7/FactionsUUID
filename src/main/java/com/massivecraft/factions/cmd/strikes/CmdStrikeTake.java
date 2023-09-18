package com.massivecraft.factions.cmd.strikes;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;

public class CmdStrikeTake extends FCommand {
    public CmdStrikeTake(){
        super();

        this.aliases.add("take");
        this.aliases.add("remove");

        this.requiredArgs.add("faction");

        this.requiredArgs.add("index");

        this.requirements = new CommandRequirements.Builder(Permission.STRIKE_MANAGE).build();
    }

    @Override
    public void perform(CommandContext context) {
        Faction faction = context.argAsFaction(0);

        int index = context.argAsInt(1);

        if(index < 0){
            return;
        }

        if(faction == null){
            return;
        }

        boolean success = faction.takeStrike(index - 1);

        if(!success){
            if(context.fPlayer != null)context.fPlayer.msg(TL.COMMAND_STRIKE_TAKE_DOESNTEXIST);
        }else{
            if(context.fPlayer != null)context.fPlayer.msg(TL.COMMAND_STRIKE_TAKE_SUCCESS, faction.describeTo(context.fPlayer));
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_STRIKE_TAKE_USAGE;
    }
}
