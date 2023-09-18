package com.massivecraft.factions.cmd.alts;

import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCmdRoot;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;

public class CmdAlts extends FCommand {
    public CmdAlts(){
        super();
        this.aliases.add("alts");
        this.aliases.add("alt");

        this.addSubCommand(new CmdAltJoin());

        this.addSubCommand(new CmdAltOpen());

        this.addSubCommand(new CmdAltClose());

        this.requirements = new CommandRequirements.Builder(Permission.ALTS).build();
    }

    @Override
    public void perform(CommandContext context) {
        context.commandChain.add(this);
        FCmdRoot.getInstance().cmdAutoHelp.execute(context);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_ALT_USAGE;
    }
}
