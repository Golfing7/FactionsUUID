package com.massivecraft.factions.cmd.tnt;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.Pair;
import com.massivecraft.factions.util.TL;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class CmdTNTFill extends FCommand {
    public CmdTNTFill() {
        super();
        this.aliases.add("fill");
        this.aliases.add("f");
        this.requiredArgs.add("radius");
        this.requiredArgs.add("amount");

        this.requirements = new CommandRequirements.Builder(Permission.TNT_FILL).withAction(PermissibleAction.TNTWITHDRAW).memberOnly().build();
    }

    @Override
    public void perform(CommandContext context) {
        if (!context.faction.equals(Board.getInstance().getFactionAt(new FLocation(context.player.getLocation())))) {
            context.msg(TL.COMMAND_TNT_TERRITORYONLY);
            return;
        }
        final int radius = context.argAsInt(0, -1);
        final int amount = context.argAsInt(1, -1);

        if (radius <= 0 || amount <= 0) {
            context.msg(TL.COMMAND_TNT_FILL_FAIL_POSITIVE);
            return;
        }

        if (radius > FactionsPlugin.getInstance().conf().commands().tnt().getMaxRadius()) {
            context.msg(TL.COMMAND_TNT_FILL_FAIL_MAXRADIUS, radius, FactionsPlugin.getInstance().conf().commands().tnt().getMaxRadius());
            return;
        }

        List<Dispenser> list = getDispensers(context.player.getLocation(), radius, context.faction.getId());

        if (amount * list.size() > context.faction.getTNTBank()) {
            context.msg(TL.COMMAND_TNT_FILL_FAIL_NEEDMORE, amount * list.size(), context.faction.getTNTBank() / list.size());
            return;
        }

        int totalCount = list.size();

        int remaining = amount * totalCount;
        int dispenserCount = 0;

        Iterator<Dispenser> iterator = list.iterator();
        while (iterator.hasNext() && remaining >= amount) {
            int left = getCount(iterator.next().getInventory().addItem(getStacks(amount)).values());
            remaining -= amount - left;
            if (((amount - left) > 0)) {
                dispenserCount++;
            }
            if (left > 0) {
                iterator.remove();
            }
        }

        context.faction.setTNTBank(context.faction.getTNTBank() - (amount * totalCount) + remaining);

        context.msg(TL.COMMAND_TNT_FILL_MESSAGE, (amount * totalCount) - remaining, dispenserCount, context.faction.getTNTBank());
    }

    static ItemStack[] getStacks(int count) {
        if (count < 65) {
            return new ItemStack[]{new ItemStack(Material.TNT, count)};
        } else {
            List<ItemStack> stack = new ArrayList<>();
            while (count > 0) {
                stack.add(new ItemStack(Material.TNT, Math.min(64, count)));
                count -= Math.min(64, count);
            }
            return stack.toArray(new ItemStack[0]);
        }
    }

    static List<Dispenser> getDispensers(Location location, int radius, String id) {
        List<Dispenser> list = new ArrayList<>();
        for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                    Block block = location.getWorld().getBlockAt(x, y, z);
                    if (!Board.getInstance().getIdAt(new FLocation(block)).equals(id)) {
                        continue;
                    }
                    if (block.getType() == Material.DISPENSER) {
                        list.add((Dispenser) block.getState());
                    }
                }
            }
        }
        return list;
    }

    static int getCount(Collection<? extends ItemStack> items) {
        int count = 0;
        for (ItemStack stack : items) {
            count += stack.getAmount();
        }
        return count;
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_TNT_FILL_DESCRIPTION;
    }
}
