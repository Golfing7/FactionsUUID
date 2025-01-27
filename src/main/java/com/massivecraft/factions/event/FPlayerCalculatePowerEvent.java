package com.massivecraft.factions.event;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import org.bukkit.event.HandlerList;

/**
 * Called when a player's power is being calculated.
 */
public class FPlayerCalculatePowerEvent extends FactionPlayerEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final double oldPower;

    public double getOldPower() {
        return oldPower;
    }

    private double powerGained;

    public double getPowerGained() {
        return powerGained;
    }

    public void setPowerGained(double powerGained) {
        this.powerGained = powerGained;
    }

    public FPlayerCalculatePowerEvent(Faction faction, FPlayer fPlayer, double oldPower, double powerGained) {
        super(faction, fPlayer, true);

        this.oldPower = oldPower;
        this.powerGained = powerGained;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
