package com.massivecraft.factions.cmd.strikes;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.event.FactionStrikeAddEvent;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import com.massivecraft.factions.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CmdStrikeGive extends FCommand {
    public CmdStrikeGive(){
        super();
        this.aliases.add("give");
        this.aliases.add("add");

        this.requiredArgs.add("faction");

        this.requiredArgs.add("reason");

        this.requirements = new CommandRequirements.Builder(Permission.STRIKE_MANAGE).noErrorOnManyArgs().build();
    }

    @Override
    public void perform(CommandContext context) {
        Faction toStrike = context.argAsFaction(0);

        if(toStrike == null){
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();

        for(int i = 1; i < context.args.size(); i++){
            stringBuilder.append(context.args.get(i)).append(" ");
        }

        String trim = stringBuilder.toString().trim();
        long now = System.currentTimeMillis();
        FactionStrikeAddEvent event = new FactionStrikeAddEvent(toStrike, context.fPlayer, trim, now);
        Bukkit.getPluginManager().callEvent(event);
        if(!event.isCancelled())
            toStrike.addStrike(event.getReason(), event.getTime());

        if(FactionsPlugin.getInstance().getConfigManager().getMainConfig().commands().strike().broadcastStrikes()){
            for(Player player : Bukkit.getOnlinePlayers()){
                FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);

                if(fPlayer == null)continue;

                player.sendMessage(TextUtil.parseColor(TL.COMMAND_STRIKE_GIVE_BROADCAST.format(toStrike.describeTo(fPlayer), trim)));
            }
        }

        if(context.fPlayer != null)context.fPlayer.msg(TL.COMMAND_STRIKE_GIVE_SUCCESS, toStrike.describeTo(context.fPlayer), trim);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_STRIKE_GIVE_USAGE;
    }
}
