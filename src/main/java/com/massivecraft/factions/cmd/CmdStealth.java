package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import com.massivecraft.factions.util.TextUtil;

public class CmdStealth extends FCommand{
    public CmdStealth(){
        this.aliases.add("stealth");
        this.aliases.add("ninja");

        this.requirements = new CommandRequirements.Builder(Permission.STEALTH).playerOnly().memberOnly().build();
    }

    @Override
    public void perform(CommandContext context) {
        FPlayer fPlayer = context.fPlayer;

        boolean stealth = fPlayer.isStealth();

        if(stealth){
            context.fPlayer.msg(TL.COMMAND_STEALTH_TOGGLE, TextUtil.parseColor("&cOFF"));
        }else{
            context.fPlayer.msg(TL.COMMAND_STEALTH_TOGGLE, TextUtil.parseColor("&aON"));
        }

        context.fPlayer.setStealth(!stealth);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_STEALTH_DESCRIPTION;
    }
}
