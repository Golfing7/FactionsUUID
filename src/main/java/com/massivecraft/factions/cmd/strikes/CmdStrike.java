package com.massivecraft.factions.cmd.strikes;

import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCmdRoot;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;

public class CmdStrike extends FCommand {
    public CmdStrike() {
        super();
        this.aliases.add("strike");

        this.addSubCommand(new CmdStrikeGive());
        this.addSubCommand(new CmdStrikeInfo());
        this.addSubCommand(new CmdStrikeTake());

        this.requirements = new CommandRequirements.Builder(Permission.STRIKE_VIEW).memberOnly().build();
    }

    @Override
    public void perform(CommandContext context) {
        context.commandChain.add(this);
        FCmdRoot.getInstance().cmdAutoHelp.execute(context);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_STRIKE_USAGE;
    }
}
