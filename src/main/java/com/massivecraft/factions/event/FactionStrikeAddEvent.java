package com.massivecraft.factions.event;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Event called when a Faction gets striked
 */
public class FactionStrikeAddEvent extends FactionEvent implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();

    private final Faction faction;
    private final FPlayer playerIssued;
    private String reason;
    private long time;
    private boolean cancelled = false;

    public FactionStrikeAddEvent(Faction faction, FPlayer playerIssued, String reason, long time)
    {
        super(faction);
        this.faction = faction;
        this.playerIssued = playerIssued;
        this.reason = reason;
        this.time = time;
    }

    public FPlayer getPlayerWhoIssued()
    {
        return playerIssued;
    }

    public String getReason()
    {
        return reason;
    }

    public long getTime()
    {
        return time;
    }

    public void setTime(long time)
    {
        this.time = time;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }

    @Override
    public boolean isCancelled()
    {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled)
    {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}