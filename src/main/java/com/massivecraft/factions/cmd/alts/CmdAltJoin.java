package com.massivecraft.factions.cmd.alts;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;

public class CmdAltJoin extends FCommand {
    public CmdAltJoin(){
        super();
        this.aliases.add("join");

        this.requiredArgs.add("faction");
        this.requiredArgs.add("joinString");

        this.requirements = new CommandRequirements.Builder(Permission.ALTS).build();
    }

    @Override
    public void perform(CommandContext context) {
        Faction faction = context.argAsFaction(0);

        String other = context.argAsString(1);

        if(faction == null)return;

        if(other == null)return;

        if (context.fPlayer.hasFaction()) {
            context.msg(TL.COMMAND_JOIN_INOTHERFACTION, context.fPlayer.describeTo(context.fPlayer, true), "your");
            return;
        }

        if (FactionsPlugin.getInstance().conf().factions().other().maxAlts() > 0 && faction.getFPlayersWhereRole(Role.ALT).size() >= FactionsPlugin.getInstance().conf().factions().other().maxAlts()) {
            context.msg(TL.COMMAND_JOIN_ATLIMIT, faction.getTag(context.fPlayer), FactionsPlugin.getInstance().conf().factions().other().maxAlts(), context.fPlayer.describeTo(context.fPlayer, false));
            return;
        }

        if(faction == context.fPlayer.getFaction()){
            context.msg(TL.COMMAND_JOIN_ALREADYMEMBER, context.fPlayer.describeTo(context.fPlayer, true), "are", faction.getTag(context.fPlayer));
            return;
        }

        if(faction.getAltJoinString().equals("-1")){
            context.msg(TL.COMMAND_ALT_NOTOPEN, faction.describeTo(context.fPlayer));
            return;
        }

        if(!faction.getAltJoinString().equals(other)){
            context.msg(TL.COMMAND_ALT_WRONGCODE, other, faction.describeTo(context.fPlayer));
            return;
        }

        context.fPlayer.resetFactionData();

        context.fPlayer.setFaction(faction);

        context.msg(TL.COMMAND_JOIN_SUCCESS, context.fPlayer.describeTo(context.fPlayer, true), faction.getTag(context.fPlayer));

        context.fPlayer.setRole(Role.ALT);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_ALT_JOINUSAGE;
    }
}
