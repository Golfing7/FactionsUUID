package com.massivecraft.factions.iface;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.perms.Relation;
import org.bukkit.ChatColor;

public interface RelationParticipator {

    String describeTo(RelationParticipator that);

    String describeTo(RelationParticipator that, boolean ucfirst);

    Relation getRelationTo(RelationParticipator that);

    Relation getRelationTo(RelationParticipator that, boolean ignorePeaceful);

    Relation getRelationToLocation(FLocation fLocation);

    ChatColor getColorTo(RelationParticipator to);
}
