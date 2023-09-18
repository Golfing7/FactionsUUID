package com.massivecraft.factions.cmd.missions;

import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCmdRoot;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;

public class CmdMission extends FCommand {
    public CmdMission(){
        super();

        this.aliases.add("missions");
        this.aliases.add("mission");

        this.requirements = new CommandRequirements.Builder(Permission.MISSIONS).memberOnly().build();
    }

    @Override
    public void perform(CommandContext context) {
        context.commandChain.add(this);
        FCmdRoot.getInstance().cmdAutoHelp.execute(context);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_MISSIONS_USAGE;
    }
}
