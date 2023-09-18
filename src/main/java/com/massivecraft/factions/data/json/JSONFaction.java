package com.massivecraft.factions.data.json;

import com.massivecraft.factions.data.MemoryFaction;
import org.bukkit.inventory.Inventory;

public class JSONFaction extends MemoryFaction {

    public JSONFaction(MemoryFaction arg0) {
        super(arg0);
    }

    private JSONFaction() {
    }

    public JSONFaction(String id) {
        super(id);
    }
}
