package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.gui.DisbandConfirmGUI;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;

import java.util.*;

public class CmdLeave extends FCommand {

    public CmdLeave() {
        super();
        this.aliases.add("leave");

        this.requirements = new CommandRequirements.Builder(Permission.LEAVE)
                .memberOnly()
                .build();
    }

    private Map<UUID, Long> confirm = new HashMap<>();

    @Override
    public void perform(CommandContext context) {
        Faction faction = context.faction;
        if (faction.isNormal() && !faction.isPermanent() && faction.getFPlayers().size() == 1 && FactionsPlugin.getInstance().getConfigManager().getMainConfig().commands().disband().isConfirmGUI()) {
            DisbandConfirmGUI disbandConfirmGUI = new DisbandConfirmGUI(context.fPlayer, faction, 3);

            disbandConfirmGUI.build();

            disbandConfirmGUI.open();
            return;
        }

        context.fPlayer.leave(true);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.LEAVE_DESCRIPTION;
    }

}
