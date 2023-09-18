package com.massivecraft.factions.cmd;

import com.massivecraft.factions.data.MemoryFPlayer;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import com.massivecraft.factions.util.TextUtil;
import com.massivecraft.factions.util.TimeFormat;
import net.coreprotect.CoreProtect;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;

import java.util.List;

public class CmdInspect extends FCommand{
    public CmdInspect(){
        super();
        this.aliases.add("inspect");

        this.optionalArgs.put("index", "1");

        this.requirements = new CommandRequirements.Builder(Permission.INSPECT).memberOnly().build();
    }

    @Override
    public void perform(CommandContext context) {
        if(context.args.size() == 0){
            context.fPlayer.setInspecting(!context.fPlayer.isInspecting());

            context.fPlayer.msg(TL.COMMAND_INSPECT_TOGGLE, context.fPlayer.isInspecting() ? TextUtil.parseColor("&aON") : TextUtil.parseColor("&cOFF"));
        }else{
            int index = context.argAsInt(0);

            if(index <= 0){
                context.fPlayer.msg(TL.COMMAND_INSPECT_BADINDEX, index);
                return;
            }

            MemoryFPlayer memoryFPlayer = (MemoryFPlayer) context.fPlayer;

            int pageIndex = (memoryFPlayer.getAllCurrentInspectData().size() / 10) + 1;
            if(memoryFPlayer.getAllCurrentInspectData() == null || pageIndex == 0 || index > pageIndex){
                context.fPlayer.msg(TL.COMMAND_INSPECT_BADINDEX, index);
                return;
            }

            context.fPlayer.msg(TL.INSPECT_HEADER, TextUtil.locationToFancyString(memoryFPlayer.getCurrentInspectLocation()));

            index--;

            for(int i = index * 10; i < i * 10 + 10 && i < memoryFPlayer.getAllCurrentInspectData().size(); i++){
                String[] strings = memoryFPlayer.getAllCurrentInspectData().get(i);
                Object[] vars = new Object[] {i + 1,
                        strings[1],
                        calcTime(Long.parseLong(strings[0])),
                        CraftMagicNumbers.getMaterial(CraftMagicNumbers.getBlock(Integer.parseInt(strings[5]))).name().toLowerCase(),
                        intToAction(Integer.parseInt(strings[7]), Integer.parseInt(strings[6]))};
                context.fPlayer.msg(TL.INSPECT_FORMAT, vars);
            }
        }
    }

    private String intToAction(int action, int subAction){
        if(subAction == 3){
            switch (action){
                case 1:
                    return "Deposited";
                case 2:
                    return "Withdrew";
            }
        }
        switch (action){
            case 0:
                return "Break";
            case 1:
                return "Place";
            case 2:
                return "Interact";
            case 3:
                return "Chest Transaction";
        }
        return "";
    }

    private String calcTime(long timeThen){
        long l = System.currentTimeMillis() / 1000L;
        long timeNow = l - timeThen;

        return TimeFormat.timeFormattedNoSecondsExtended((int) (timeNow / 60), true);
    }

    private List<String[]> getCoreProtectData(Block block){
        return CoreProtect.getInstance().getAPI().blockLookup(block, Integer.MAX_VALUE);
    }

    @Override
    public TL getUsageTranslation() {
        return TL.COMMAND_INSPECT_DESCRIPTION;
    }
}
