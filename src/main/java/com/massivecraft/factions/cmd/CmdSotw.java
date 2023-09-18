package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import org.bukkit.ChatColor;

public class CmdSotw extends FCommand{
    public CmdSotw(){
        super();

        this.aliases.add("sotw");

        this.requirements = new CommandRequirements.Builder(Permission.SOTW).build();
    }

    @Override
    public void perform(CommandContext context) {
        boolean on = !FactionsPlugin.getInstance().isSOTW();

        FactionsPlugin.getInstance().setSOTW(on);

        if(on){
            context.sender.sendMessage(ChatColor.GREEN + "Turned on SOTW.");

            for(FPlayer fPlayer : FPlayers.getInstance().getOnlinePlayers()){
                if(Permission.SOTW.has(fPlayer.getPlayer()))continue;
                fPlayer.msg(TL.PLAYER_SOTW_NOFLY);
                fPlayer.setFlying(false, false);
                fPlayer.setAutoFlying(false);
            }
        }else{
            context.sender.sendMessage(ChatColor.RED + "Turned off SOTW");
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_SOTW_DESCRIPTION;
    }
}
