package com.massivecraft.factions.listeners;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionsPlugin;
import com.massivecraft.factions.config.file.MainConfig;
import com.massivecraft.factions.perms.PermissibleAction;
import com.massivecraft.factions.perms.Relation;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.TL;
import com.massivecraft.factions.util.UpgradeType;
import com.massivecraft.factions.util.material.MaterialDb;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class FactionsBlockListener implements Listener {

    public FactionsPlugin plugin;

    public FactionsBlockListener(FactionsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!plugin.worldUtil().isEnabled(event.getBlock().getWorld())) {
            return;
        }

        if (!event.canBuild()) {
            return;
        }

        // special case for flint&steel, which should only be prevented by DenyUsage list
        if (event.getBlockPlaced().getType() == Material.FIRE) {
            return;
        }

        Faction targetFaction = Board.getInstance().getFactionAt(new FLocation(event.getBlock().getLocation()));
        if (targetFaction.isNormal() && !targetFaction.isPeaceful() && FactionsPlugin.getInstance().conf().factions().specialCase().getIgnoreBuildMaterials().contains(event.getBlock().getType())) {
            return;
        }

        if (!playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock().getLocation(), PermissibleAction.BUILD, false)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        if (!plugin.worldUtil().isEnabled(event.getBlock().getWorld())) {
            return;
        }

        if(event.getToBlock().getLocation().getZ() == event.getBlock().getLocation().getZ() &&
        event.getToBlock().getLocation().getX() == event.getBlock().getLocation().getX())return;

        if (!FactionsPlugin.getInstance().conf().exploits().isLiquidFlow()) {
            return;
        }
        if (event.getBlock().isLiquid()) {
            if (event.getToBlock().isEmpty()) {
                Faction from = Board.getInstance().getFactionAt(new FLocation(event.getBlock()));
                Faction to = Board.getInstance().getFactionAt(new FLocation(event.getToBlock()));
                if (from == to) {
                    // not concerned with inter-faction events
                    return;
                }
                // from faction != to faction
                if (to.isNormal()) {
                    if (from.isNormal() && from.getRelationTo(to).isAlly()) {
                        return;
                    }
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!plugin.worldUtil().isEnabled(event.getBlock().getWorld())) {
            return;
        }

        if (FactionsPlugin.getInstance().conf().factions().protection().getBreakExceptions().contains(event.getBlock().getType()) &&
                Board.getInstance().getFactionAt(new FLocation(event.getBlock().getLocation())).isNormal()) {
            return;
        }

        if (!playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock().getLocation(), PermissibleAction.DESTROY, false)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockDamage(BlockDamageEvent event) {
        if (!plugin.worldUtil().isEnabled(event.getBlock().getWorld())) {
            return;
        }

        if (FactionsPlugin.getInstance().conf().factions().protection().getBreakExceptions().contains(event.getBlock().getType()) &&
                Board.getInstance().getFactionAt(new FLocation(event.getBlock().getLocation())).isNormal()) {
            return;
        }

        if (event.getInstaBreak() && !playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock().getLocation(), PermissibleAction.DESTROY, false)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        if (!plugin.worldUtil().isEnabled(event.getBlock().getWorld())) {
            return;
        }

        if (!FactionsPlugin.getInstance().conf().factions().protection().isPistonProtectionThroughDenyBuild()) {
            return;
        }

        // if the pushed blocks list is empty, no worries
        if (event.getBlocks().isEmpty()) {
            return;
        }

        Faction pistonFaction = Board.getInstance().getFactionAt(new FLocation(event.getBlock()));

        if (!canPistonMoveBlock(pistonFaction, event.getBlocks(), event.getDirection())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        if (!plugin.worldUtil().isEnabled(event.getBlock().getWorld())) {
            return;
        }

        // if not a sticky piston, retraction should be fine
        if (!event.isSticky() || !FactionsPlugin.getInstance().conf().factions().protection().isPistonProtectionThroughDenyBuild()) {
            return;
        }

        List<Block> blocks;
        if (FactionsPlugin.getMCVersion() < 800) {
            blocks = Collections.singletonList(event.getBlock().getRelative(event.getDirection(), 2));
        } else {
            blocks = event.getBlocks();
        }

        // if the retracted blocks list is empty, no worries
        if (blocks.isEmpty()) {
            return;
        }

        Faction pistonFaction = Board.getInstance().getFactionAt(new FLocation(event.getBlock()));

        if (!canPistonMoveBlock(pistonFaction, blocks, null)) {
            event.setCancelled(true);
        }
    }

    private static Material spawnerMaterial;

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onSpawnerPlace(BlockPlaceEvent event) {
        if(spawnerMaterial == null) {
            spawnerMaterial = Material.getMaterial("MOB_SPAWNER");
            if(spawnerMaterial == null) {
                spawnerMaterial = Material.getMaterial("SPAWNER"); //Newer version support.
            }
        }

        FLocation fLocation = new FLocation(event.getBlock());
        Faction factionAt = Board.getInstance().getFactionAt(fLocation);
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(event.getPlayer());
        if(fPlayer.isAdminBypassing())
            return;

        if (factionAt != null && !factionAt.isWilderness() &&
                FactionsPlugin.getInstance().getConfigManager().getMainConfig().factions().spawning().isForceSpawnersToBePlacedInSpawnerChunks()) {
            FLocation chunkFLoc = new FLocation(event.getBlock().getChunk());
            if (!factionAt.isSpawnerChunk(chunkFLoc)) {
                fPlayer.msg(TL.SPAWNERS_CANTPLACEOUTSIDESPAWNERCHUNKS);
                event.setCancelled(true);
                return;
            }
        }

        if(FactionsPlugin.getInstance().getConfigManager().getMainConfig().factions().spawning().isAllowSpawnerPlacementInWild()
                || event.getBlock().getType() != spawnerMaterial)
            return;

        //Get the faction at the location.
        if(factionAt == null || !factionAt.isWilderness())
            return;

        event.setCancelled(true);
        fPlayer.msg(TL.SPAWNERS_CANTPLACEINWILD);
    }

    private boolean canPistonMoveBlock(Faction pistonFaction, List<Block> blocks, BlockFace direction) {
        String world = blocks.get(0).getWorld().getName();
        List<Faction> factions = (direction == null ? blocks.stream() : blocks.stream().map(b -> b.getRelative(direction)))
                .map(Block::getLocation)
                .map(FLocation::new)
                .distinct()
                .map(Board.getInstance()::getFactionAt)
                .distinct()
                .collect(Collectors.toList());

        boolean disableOverall = FactionsPlugin.getInstance().conf().factions().other().isDisablePistonsInTerritory();
        for (Faction otherFaction : factions) {
            if (pistonFaction == otherFaction) {
                continue;
            }
            // Check if the piston is moving in a faction's territory. This disables pistons entirely in faction territory.
            if (disableOverall && otherFaction.isNormal()) {
                return false;
            }
            if (otherFaction.isWilderness() && FactionsPlugin.getInstance().conf().factions().protection().isWildernessDenyBuild() && !FactionsPlugin.getInstance().conf().factions().protection().getWorldsNoWildernessProtection().contains(world)) {
                return false;
            } else if (otherFaction.isSafeZone() && FactionsPlugin.getInstance().conf().factions().protection().isSafeZoneDenyBuild()) {
                return false;
            } else if (otherFaction.isWarZone() && FactionsPlugin.getInstance().conf().factions().protection().isWarZoneDenyBuild()) {
                return false;
            }
            Relation rel = pistonFaction.getRelationTo(otherFaction);
            if (!otherFaction.hasAccess((!FactionsPlugin.getInstance().conf().onlineChecks().doOnlineChecks() || otherFaction.hasPlayersOnline()), rel, PermissibleAction.BUILD)) {
                return false;
            }
        }
        return true;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onFrostWalker(EntityBlockFormEvent event) {
        if (!plugin.worldUtil().isEnabled(event.getBlock().getWorld())) {
            return;
        }

        if (event.getEntity() == null || event.getEntity().getType() != EntityType.PLAYER || event.getBlock() == null) {
            return;
        }

        Player player = (Player) event.getEntity();
        Location location = event.getBlock().getLocation();

        // only notify every 10 seconds
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
        boolean justCheck = fPlayer.getLastFrostwalkerMessage() + 10000 > System.currentTimeMillis();
        if (!justCheck) {
            fPlayer.setLastFrostwalkerMessage();
        }

        // Check if they have build permissions here. If not, block this from happening.
        if (!playerCanBuildDestroyBlock(player, location, PermissibleAction.FROSTWALK, justCheck)) {
            event.setCancelled(true);
        }
    }

    private static Material SUGAR_CANE_BLOCK;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onGrow(BlockGrowEvent e){
        if(SUGAR_CANE_BLOCK == null){
            SUGAR_CANE_BLOCK = MaterialDb.get("SUGAR_CANE");
        }

        if(!FactionsPlugin.getInstance().conf().upgrades().cropBoost().isEnabled())return;

        if (e.getBlock().getType() != SUGAR_CANE_BLOCK)
            return;

        int i;

        for (i = 1; e.getBlock().getRelative(BlockFace.DOWN, i).getType() == SUGAR_CANE_BLOCK; ++i) {
            ;
        }

        if(i >= 3)return;

        FLocation fLocation = new FLocation(e.getBlock());

        Faction factionAt = Board.getInstance().getFactionAt(fLocation);

        if(factionAt.isWilderness())return;

        int upgrade = factionAt.getUpgrade(UpgradeType.CROP_BOOST);

        if(upgrade <= 0 && FactionsPlugin.getInstance().conf().upgrades().cropBoost().getDefaultBoost() == 0.0D)return;

        double boost = FactionsPlugin.getInstance().conf().upgrades().cropBoost().getNumber(upgrade);

        float randomFloat = ThreadLocalRandom.current().nextFloat();

        if(boost >= randomFloat){
            Block relative = e.getBlock().getRelative(BlockFace.UP);

            if(relative.getType() == Material.AIR){
                relative.setType(SUGAR_CANE_BLOCK, false);
            }
        }
    }

    public static boolean playerCanBuildDestroyBlock(Player player, Location location, PermissibleAction permissibleAction, boolean justCheck) {
        String name = player.getName();
        MainConfig conf = FactionsPlugin.getInstance().conf();
        if (conf.factions().protection().getPlayersWhoBypassAllProtection().contains(name)) {
            return true;
        }

        FPlayer me = FPlayers.getInstance().getById(player.getUniqueId().toString());
        if (me.isAdminBypassing()) {
            return true;
        }

        FLocation loc = new FLocation(location);
        Faction otherFaction = Board.getInstance().getFactionAt(loc);

        if (otherFaction.isWilderness()) {
            if (conf.worldGuard().isBuildPriority() && FactionsPlugin.getInstance().getWorldguard() != null && FactionsPlugin.getInstance().getWorldguard().playerCanBuild(player, location)) {
                return true;
            }

            if (!conf.factions().protection().isWildernessDenyBuild() || conf.factions().protection().getWorldsNoWildernessProtection().contains(location.getWorld().getName())) {
                return true; // This is not faction territory. Use whatever you like here.
            }

            if (!justCheck) {
                me.msg(TL.PERM_DENIED_WILDERNESS, permissibleAction.getShortDescription());
            }

            return false;
        } else if (otherFaction.isSafeZone()) {
            if (conf.worldGuard().isBuildPriority() && FactionsPlugin.getInstance().getWorldguard() != null && FactionsPlugin.getInstance().getWorldguard().playerCanBuild(player, location)) {
                return true;
            }

            if (!conf.factions().protection().isSafeZoneDenyBuild() || Permission.MANAGE_SAFE_ZONE.has(player)) {
                return true;
            }

            if (!justCheck) {
                me.msg(TL.PERM_DENIED_SAFEZONE, permissibleAction.getShortDescription());
            }

            return false;
        } else if (otherFaction.isWarZone()) {
            if (conf.worldGuard().isBuildPriority() && FactionsPlugin.getInstance().getWorldguard() != null && FactionsPlugin.getInstance().getWorldguard().playerCanBuild(player, location)) {
                return true;
            }

            if (!conf.factions().protection().isWarZoneDenyBuild() || Permission.MANAGE_WAR_ZONE.has(player)) {
                return true;
            }

            if (!justCheck) {
                me.msg(TL.PERM_DENIED_WARZONE, permissibleAction.getShortDescription());
            }

            return false;
        }
        if (FactionsPlugin.getInstance().getLandRaidControl().isRaidable(otherFaction)) {
            return true;
        }

        Faction myFaction = me.getFaction();
        boolean pain = !justCheck && otherFaction.hasAccess(me, PermissibleAction.PAINBUILD);

        // If the faction hasn't: defined access or denied, fallback to config values
        if (!otherFaction.hasAccess(me, permissibleAction)) {
            if (pain && permissibleAction != PermissibleAction.FROSTWALK) {
                player.damage(conf.factions().other().getActionDeniedPainAmount());
                me.msg(TL.PERM_DENIED_PAINTERRITORY, permissibleAction.getShortDescription(), otherFaction.getTag(myFaction));
                return true;
            } else if (!justCheck) {
                me.msg(TL.PERM_DENIED_TERRITORY, permissibleAction.getShortDescription(), otherFaction.getTag(myFaction));
            }
            return false;
        }

        // Also cancel and/or cause pain if player doesn't have ownership rights for this claim
        if (conf.factions().ownedArea().isEnabled() && (conf.factions().ownedArea().isDenyBuild() || conf.factions().ownedArea().isPainBuild()) && !otherFaction.playerHasOwnershipRights(me, loc)) {
            if (pain && conf.factions().ownedArea().isPainBuild()) {
                player.damage(conf.factions().other().getActionDeniedPainAmount());

                if (!conf.factions().ownedArea().isDenyBuild()) {
                    me.msg(TL.PERM_DENIED_PAINOWNED, permissibleAction.getShortDescription(), otherFaction.getOwnerListString(loc));
                }
            }
            if (conf.factions().ownedArea().isDenyBuild()) {
                if (!justCheck) {
                    me.msg(TL.PERM_DENIED_OWNED, permissibleAction.getShortDescription(), otherFaction.getOwnerListString(loc));
                }

                return false;
            }
        }

        return true;
    }
}
