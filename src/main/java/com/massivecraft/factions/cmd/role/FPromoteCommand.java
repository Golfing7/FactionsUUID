package com.massivecraft.factions.cmd.role;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.event.FPlayerPromoteEvent;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import org.bukkit.Bukkit;

public class FPromoteCommand extends FCommand {

    public int relative = 0;

    public FPromoteCommand() {
        super();

        this.requiredArgs.add("player");

        this.requirements = new CommandRequirements.Builder(Permission.PROMOTE)
                .memberOnly()
                .withAction(PermissibleAction.PROMOTE)
                .build();
    }

    @Override
    public void perform(CommandContext context) {
        FPlayer target = context.argAsBestFPlayerMatch(0);
        if (target == null) {
            // context.msg(TL.GENERIC_NOPLAYERFOUND, context.argAsString(0));
            return;
        }

        if(target.getRole() == Role.ALT){
            context.msg(TL.COMMAND_PROMOTE_ISALT);
            return;
        }

        if (!target.getFaction().equals(context.faction) && !context.fPlayer.isAdminBypassing()) {
            context.msg(TL.COMMAND_PROMOTE_WRONGFACTION, target.getName());
            return;
        }

        Role current = target.getRole();
        Role promotion = Role.getRelative(current, +relative);

        if (promotion == null) {
            context.msg(TL.COMMAND_PROMOTE_NOTTHATPLAYER);
            return;
        }

        // Don't allow people to promote people to their same or higher rnak.
        if (context.fPlayer.getRole().value <= promotion.value && !context.fPlayer.isAdminBypassing()) {
            context.msg(TL.COMMAND_PROMOTE_NOT_ALLOWED);
            return;
        }

        TL actionTL = relative > 0 ? TL.COMMAND_PROMOTE_PROMOTED : TL.COMMAND_PROMOTE_DEMOTED;
        String action = actionTL.toString();

        // Success!
        Bukkit.getPluginManager().callEvent(new FPlayerPromoteEvent(target, actionTL, current, promotion));
        target.setRole(promotion);
        if (target.isOnline()) {
            target.msg(TL.COMMAND_PROMOTE_TARGET, action, promotion.nicename);
        }

        context.msg(TL.COMMAND_PROMOTE_SUCCESS, action, target.getName(), promotion.nicename);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_PROMOTE_DESCRIPTION;
    }

}
