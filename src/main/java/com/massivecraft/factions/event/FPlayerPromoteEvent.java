package com.massivecraft.factions.event;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.perms.Role;
import com.massivecraft.factions.util.TL;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a FPlayer is promoted.
 */
public class FPlayerPromoteEvent extends Event
{

    private static final HandlerList handlers = new HandlerList();

    private final FPlayer promoted;
    private final TL action;
    private final Role oldRole;
    private final Role newRole;

    public FPlayerPromoteEvent(FPlayer promoted, TL action, Role oldRole, Role newRole)
    {
        this.promoted = promoted;
        this.action = action;
        this.oldRole = oldRole;
        this.newRole = newRole;
    }

    public FPlayer getFPlayer()
    {
        return promoted;
    }

    public TL getAction()
    {
        return action;
    }

    public Role getOldRole()
    {
        return oldRole;
    }

    public Role getNewRole()
    {
        return newRole;
    }

    public Player getPlayer()
    {
        return promoted.getPlayer();
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
