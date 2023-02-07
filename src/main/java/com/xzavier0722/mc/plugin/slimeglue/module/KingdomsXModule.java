package com.xzavier0722.mc.plugin.slimeglue.module;

import com.xzavier0722.mc.plugin.slimeglue.api.ACompatibilityModule;
import com.xzavier0722.mc.plugin.slimeglue.api.listener.ISlimefunAndroidListener;
import com.xzavier0722.mc.plugin.slimeglue.api.protection.IBlockProtectionHandler;
import com.xzavier0722.mc.plugin.slimeglue.api.protection.IPlayerProtectionHandler;
import io.github.thebusybiscuit.slimefun4.api.events.AndroidMineEvent;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.kingdoms.constants.group.Kingdom;
import org.kingdoms.constants.land.Land;
import org.kingdoms.constants.land.location.SimpleChunkLocation;
import org.kingdoms.constants.land.location.SimpleLocation;
import org.kingdoms.constants.land.structures.Structure;
import org.kingdoms.constants.land.turrets.Turret;

public class KingdomsXModule extends ACompatibilityModule {

    public KingdomsXModule() {
        addProtectionHandler(new IBlockProtectionHandler() {
            @Override
            public boolean canPlaceBlock(OfflinePlayer player, Location location) {
                verbose("canPlaceBlock: player=" + player.getName() + ", loc=" + location);
                return canAccess(player, location);
            }

            @Override
            public boolean canBreakBlock(OfflinePlayer player, Location location) {
                verbose("canBreakBlock: player=" + player.getName() + ", loc=" + location);
                return canAccess(player, location);
            }

            @Override
            public boolean canInteractBlock(OfflinePlayer player, Location location) {
                verbose("canInteractBlock: player=" + player.getName() + ", loc=" + location);
                return canAccess(player, location);
            }
        });

        addProtectionHandler(new IPlayerProtectionHandler() {
            @Override
            public boolean canAttackPlayer(OfflinePlayer player, Location location) {
                verbose("canAttackPlayer: player=" + player.getName() + ", loc=" + location);
                return canAccess(player, location);
            }

            @Override
            public boolean canAttackEntity(OfflinePlayer player, Location location) {
                verbose("canAttackEntity: player=" + player.getName() + ", loc=" + location);
                return canAccess(player, location);
            }

            @Override
            public boolean canInteractEntity(OfflinePlayer player, Location location) {
                verbose("canInteractEntity: player=" + player.getName() + ", loc=" + location);
                return canAccess(player, location);
            }
        });

        addListener(new ISlimefunAndroidListener() {
            @Override
            public void onMine(AndroidMineEvent e) {
                var b = e.getBlock();
                verbose("onMine: " + b.getLocation());
                Land land = SimpleChunkLocation.of(b).getLand();
                if (land == null || !land.isClaimed()) {
                    return;
                }

                SimpleLocation loc = SimpleLocation.of(e.getBlock());
                Structure structure = land.getStructures().get(loc);
                if (structure != null && structure.getLocation().equals(loc)) {
                    e.setCancelled(true);
                    return;
                }

                Turret turret = land.getTurrets().get(loc);
                if (turret != null) {
                    e.setCancelled(true);
                }
            }
        });
    }

    private boolean canAccess(OfflinePlayer p, Location l) {
        Land land = SimpleChunkLocation.of(l).getLand();
        verbose("canAccess: " + land);
        if (land == null || !land.isClaimed()) {
            return true;
        }

        Kingdom kingdom = land.getKingdom();
        return verbose(
                "canAccess: ret=",
                kingdom == null || p.getUniqueId().equals(kingdom.getKingId()) || kingdom.isMember(p)
        );
    }

    @Override
    public String getCompatibilityPluginName() {
        return "Kingdoms";
    }

    @Override
    public void enable(Plugin plugin) {

    }

    @Override
    public void disable() {

    }
}
