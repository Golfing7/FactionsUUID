package com.massivecraft.factions.cmd;

import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;

public class CmdSetPaypal extends FCommand{

    public CmdSetPaypal(){
        super();
        this.aliases.add("setpaypal");

        this.requiredArgs.add("paypal link");

        this.requirements = new CommandRequirements.Builder(Permission.SETPAYPAL).memberOnly().build();
    }

    @Override
    public void perform(CommandContext context) {
        if(context.fPlayer.getRole() != Role.ADMIN && !context.fPlayer.isAdminBypassing()){
            context.fPlayer.msg(TL.COMMAND_SETPAYPAL_NOTADMIN);
            return;
        }

        String payPalLink = context.argAsString(0);

        if(payPalLink == null || payPalLink.isEmpty()){
            context.fPlayer.msg(TL.COMMAND_SETPAYPAL_NOTVALID, "null");
            return;
        }

        if(!payPalLink.contains("@") || !payPalLink.contains(".")){
            context.fPlayer.msg(TL.COMMAND_SETPAYPAL_NOTVALID, payPalLink);
            return;
        }

        context.fPlayer.getFaction().setPayPal(payPalLink);

        context.fPlayer.msg(TL.COMMAND_SETPAYPAL_SUCCESS, payPalLink);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SETPAYPAL_DESCRIPTION;
    }
}
