package com.massivecraft.factions.util;

import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FactionsPlugin;

public abstract class AsyncSpiralTask extends SpiralTask{
    public AsyncSpiralTask(FLocation fLocation, int radius) {
        super(fLocation, radius, 0);

        this.runTaskTimerAsynchronously(FactionsPlugin.getInstance(), 0, 1);

        this.setTaskID(this.getTaskId());

        this.timeout = 40;
    }
}
