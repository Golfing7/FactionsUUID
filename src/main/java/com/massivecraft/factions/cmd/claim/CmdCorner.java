package com.massivecraft.factions.cmd.claim;

import com.massivecraft.factions.*;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import net.royawesome.jlibnoise.MathHelper;
import org.bukkit.Location;

public class CmdCorner extends FCommand {
    public CmdCorner(){
        this.aliases.add("claimcorner");
        this.aliases.add("corner");

        this.requirements = new CommandRequirements.Builder(Permission.CLAIMCORNER)
                .withAction(PermissibleAction.CLAIMCORNER)
                .playerOnly()
                .build();
    }

    enum EnumCorner{
        POS_POS,
        NEG_NEG,
        POS_NEG,
        NEG_POS
    }

    @Override
    public void perform(CommandContext context) {
        FPlayer fPlayer = context.fPlayer;

        if(!FactionsPlugin.getInstance().isSOTW() && FactionsPlugin.getInstance().conf().corner().onlyAllowInSOTW()){
            fPlayer.msg(TL.COMMAND_CORNER_ISNTSOTW);
            return;
        }

        EnumCorner corner = getCorner(fPlayer);

        if(corner == null){
            fPlayer.msg(TL.COMMAND_CORNER_MUSTBEINCORNER);
            return;
        }

        if(fPlayer.getRelationToLocation() != Relation.MEMBER && !Board.getInstance().getFactionAt(new FLocation(fPlayer)).isWilderness()){
            fPlayer.msg(TL.COMMAND_CORNER_MUSTOWN);
            return;
        }

        int xStart = corner == EnumCorner.NEG_NEG || corner == EnumCorner.NEG_POS ? 0 : -FactionsPlugin.getInstance().conf().corner().diameterClaim();
        int zStart = corner == EnumCorner.NEG_NEG || corner == EnumCorner.POS_NEG ? 0 : -FactionsPlugin.getInstance().conf().corner().diameterClaim();
        int xEnd = xStart + FactionsPlugin.getInstance().conf().corner().diameterClaim();
        int zEnd = zStart + FactionsPlugin.getInstance().conf().corner().diameterClaim();

        Location location = fPlayer.getPlayer().getLocation();
        int myCX = location.getBlockX() >> 4;
        int myCZ = location.getBlockZ() >> 4;

        Faction faction = fPlayer.getFaction();
        int landRounded = faction.getLandRounded();
        for(int x = xStart; x <= xEnd; x++){
            for(int z = zStart; z <= zEnd; z++){
                if (faction.getPowerRounded() <= landRounded) {
                    fPlayer.msg(TL.COMMAND_CORNER_NOMOREPOWER);
                    return;
                }
                Board.getInstance().setFactionAt(faction, new FLocation(fPlayer.getPlayer().getWorld().getName(), myCX + x, myCZ + z));
                landRounded++;
            }
        }

        fPlayer.msg(TL.COMMAND_CORNER_SUCCESS);
    }

    private EnumCorner getCorner(FPlayer fPlayer){
        Location location = fPlayer.getPlayer().getLocation();
        int cx = location.getBlockX() >> 4;
        int cz = location.getBlockZ() >> 4;

        Location center = location.getWorld().getWorldBorder().getCenter();

        double radius = (location.getWorld().getWorldBorder().getSize() / 2D);

            double maxX = MathHelper.floor((center.getX() + radius - 0.1) / 16.0D);
            double maxZ = MathHelper.floor((center.getZ() + radius - 0.1) / 16.0D);

        double minX = MathHelper.floor((center.getX() - radius) / 16.0D);
        double minZ = MathHelper.floor((center.getZ() - radius) / 16.0D);

        if(cx == maxX && cz == maxZ)return EnumCorner.POS_POS;
        if(cx == maxX && cz == minZ)return EnumCorner.POS_NEG;
        if(cx == minX && cz == maxZ)return EnumCorner.NEG_POS;
        if(cx == minX && cz == minZ)return EnumCorner.NEG_NEG;
        return null;
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_CORNER_DESCRIPTION;
    }
}
