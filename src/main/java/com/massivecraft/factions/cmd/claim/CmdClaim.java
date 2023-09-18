package com.massivecraft.factions.cmd.claim;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.*;

import java.util.*;


public class CmdClaim extends FCommand {

    public CmdClaim() {
        super();
        this.aliases.add("claim");

        //this.requiredArgs.add("");
        this.optionalArgs.put("radius", "1");
        this.optionalArgs.put("faction", "your");

        this.requirements = new CommandRequirements.Builder(Permission.CLAIM)
                .playerOnly()
                .build();
    }

    public static final Map<UUID, Triplet<Integer, Faction, Long>> attemptingBypass = new HashMap<>();

    @Override
    public void perform(final CommandContext context) {
        // Read and validate input
        int radius = context.argAsInt(0, 1); // Default to 1
        Faction forFaction = context.argAsFaction(1, context.faction); // Default to own
        String arg = context.argAsString(0);

        boolean bypassed = false;

        if(arg != null && !arg.isEmpty() && arg.equalsIgnoreCase("confirm")){
            if(!attemptingBypass.containsKey(context.player.getUniqueId())){
                context.msg("&cYou don't have a claim to confirm!");
                return;
            }
            Triplet<Integer, Faction, Long> integerLongPair = attemptingBypass.get(context.player.getUniqueId());
            long timeAt = integerLongPair.getRight();

            if(timeAt + 5000 < System.currentTimeMillis()){
                context.msg("&cThat claim confirm has expired! Try again!");
                attemptingBypass.remove(context.player.getUniqueId());
                return;
            }

            radius = integerLongPair.getLeft();

            forFaction = integerLongPair.getMiddle();

            bypassed = true;
        }

        if (radius < 1) {
            context.msg(TL.COMMAND_CLAIM_INVALIDRADIUS);
            return;
        }

        if(radius > FactionsPlugin.getInstance().conf().factions().claims().getMaxRadiusClaim() && !bypassed){
            if(context.fPlayer.isAdminBypassing() || context.player.hasPermission("factions.claim.bypasslimit")){
                attemptingBypass.put(context.player.getUniqueId(), Triplet.of(radius, forFaction, System.currentTimeMillis()));
                context.msg(TL.COMMAND_CLAIM_TOOBIGCONFIRM, radius);
                return;
            }
            context.msg(TL.COMMAND_CLAIM_TOOBIG, FactionsPlugin.getInstance().conf().factions().claims().getMaxRadiusClaim());
            return;
        }

        if (radius < 2) {
            // single chunk
            context.fPlayer.attemptClaim(forFaction, context.player.getLocation(), true);
        } else {
            // radius claim
            if (!Permission.CLAIM_RADIUS.has(context.sender, false)) {
                context.msg(TL.COMMAND_CLAIM_DENIED);
                return;
            }

            FLocation fLocation = new FLocation(context.player);

            context.msg(TL.COMMAND_CLAIM_RADIUS, radius, fLocation.getChunk().getX(), fLocation.getChunk().getZ(), forFaction.describeTo(context.fPlayer, true));

            final Faction loopFac = forFaction;

            new SpiralTask(fLocation, radius) {
                private int failCount = 0;
                private final int limit = FactionsPlugin.getInstance().conf().factions().claims().getRadiusClaimFailureLimit() - 1;

                private int claimed = 0;

                @Override
                public boolean work() {
                    boolean success = context.fPlayer.attemptClaim(loopFac, this.currentFLocation(), true, this);
                    if (success) {
                        failCount = 0;
                        claimed++;
                    } else return failCount++ < limit;

                    return true;
                }

                @Override
                public void finish() {
                    Set<FPlayer> informTheseFPlayers = new HashSet<>(loopFac.getFPlayersWhereOnline(true));
                    informTheseFPlayers.remove(context.fPlayer);
                    for (FPlayer fp : informTheseFPlayers) {
                        fp.msg(TL.CLAIM_CLAIMEDAMOUNT, context.fPlayer.describeTo(fp, true), claimed, loopFac.describeTo(fp));
                    }

                    context.fPlayer.msg(TL.CLAIM_CLAIMEDAMOUNT, context.fPlayer.describeTo(context.fPlayer, true), claimed, loopFac.describeTo(context.fPlayer));
                    super.finish();
                }
            };
        }
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_CLAIM_DESCRIPTION;
    }

}
