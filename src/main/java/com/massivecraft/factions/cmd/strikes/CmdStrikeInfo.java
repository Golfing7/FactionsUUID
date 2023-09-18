package com.massivecraft.factions.cmd.strikes;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.StrikeInfo;
import com.massivecraft.factions.util.TL;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class CmdStrikeInfo extends FCommand {

    public CmdStrikeInfo(){
        super();
        this.aliases.add("list");
        this.aliases.add("info");
        this.aliases.add("view");

        this.optionalArgs.put("faction", "your faction");

        this.requirements = new CommandRequirements.Builder(Permission.STRIKE_VIEW).playerOnly().build();
    }

    @Override
    public void perform(CommandContext context) {
        FPlayer fPlayer = context.fPlayer;

        Faction faction = context.args.size() != 0 && Permission.STRIKE_MANAGE.has(fPlayer.getPlayer()) ? context.argAsFaction(0) : context.fPlayer.getFaction();

        if(faction == null){
            if(fPlayer.hasFaction()){
                fPlayer.msg(TL.GENERIC_MEMBERONLY);
            }else{
                fPlayer.msg(TL.COMMAND_STRIKE_LIST_NOFACTION);
            }
            return;
        }

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(FactionsPlugin.getInstance().conf().getTimeZone()));

        List<StrikeInfo> strikes = faction.getStrikes();
        if(strikes == null || strikes.isEmpty()){
            fPlayer.msg(TL.COMMAND_STRIKE_LIST_BLAMELESS, faction.describeTo(fPlayer));
            return;
        }

        fPlayer.msg(TL.COMMAND_STRIKE_LIST_HEADER, faction.describeTo(fPlayer));

        int i = 1;

        for(StrikeInfo strikeInfo : strikes){
            ZonedDateTime wayBackWhen = now.minus(System.currentTimeMillis() - strikeInfo.getGivenAt(), ChronoUnit.MILLIS);

            fPlayer.msg(TL.COMMAND_STRIKE_LIST_FORMAT, i++, strikeInfo.getStrikeReason(), wayBackWhen.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_STRIKE_LIST_USAGE;
    }
}
