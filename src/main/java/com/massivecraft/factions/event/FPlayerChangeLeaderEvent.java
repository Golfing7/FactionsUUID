package com.massivecraft.factions.event;

import com.massivecraft.factions.FPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a FPlayer is promoted to admin role.
 */
public class FPlayerChangeLeaderEvent extends Event
{

    private static final HandlerList handlers = new HandlerList();

    private final FPlayer newAdmin;
    private final FPlayer oldAdmin;

    public FPlayerChangeLeaderEvent(FPlayer newAdmin, FPlayer oldAdmin)
    {
        this.newAdmin = newAdmin;
        this.oldAdmin = oldAdmin;
    }

    public FPlayer getNewAdmin()
    {
        return newAdmin;
    }

    public FPlayer getOldAdmin()
    {
        return oldAdmin;
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
