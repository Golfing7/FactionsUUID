package com.massivecraft.factions.event;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.perms.Relation;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Event called when a Faction relation is called.
 */
public class FactionRelationEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final FPlayer fPlayer;
    private final Faction fsender;
    private final Faction ftarget;
    private final Relation foldrel;
    private final Relation frel;

    public FactionRelationEvent(Faction sender, Faction target, Relation oldrel, Relation rel) {
        fPlayer = null;
        fsender = sender;
        ftarget = target;
        foldrel = oldrel;
        frel = rel;
    }

    public FactionRelationEvent(FPlayer fPlayer, Faction sender, Faction target, Relation oldrel, Relation rel) {
        this.fPlayer = fPlayer;
        fsender = sender;
        ftarget = target;
        foldrel = oldrel;
        frel = rel;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public FPlayer getfPlayer() {
        return fPlayer;
    }

    public Relation getOldRelation() {
        return foldrel;
    }

    public Relation getRelation() {
        return frel;
    }

    public Faction getFaction() {
        return fsender;
    }

    public Faction getTargetFaction() {
        return ftarget;
    }
}
