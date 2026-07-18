package com.canoestudio.sofcore.compat;

import com.canoestudio.sofcore.SOFConfig;
import com.canoestudio.sofcore.SOFcore;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xaero.common.XaeroMinimapSession;
import xaero.common.core.IXaeroMinimapClientPlayNetHandler;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.common.minimap.waypoints.WaypointWorld;
import xaero.common.minimap.waypoints.WaypointsManager;
import xaero.hud.minimap.waypoint.WaypointColor;
import xaero.hud.minimap.waypoint.set.WaypointSet;
import xaero.hud.minimap.world.MinimapWorld;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SideOnly(Side.CLIENT)
public final class WaystonesToXaeroCompat {

    private static final String WAYSTONES_MOD_ID = "waystones";

    private WaystonesToXaeroCompat() {
    }

    @SubscribeEvent
    public static void onRightClickWaystone(PlayerInteractEvent.RightClickBlock event) {
        if (!SOFConfig.waystonesToXaero.enabled || event.getHand() != EnumHand.MAIN_HAND) {
            return;
        }

        World world = event.getWorld();
        if (world == null || !world.isRemote || !isWaystoneBlock(world, event.getPos())) {
            return;
        }

        TileEntity tile = findWaystoneTile(world, event.getPos());
        String name = getWaystoneName(tile);
        if (name == null || name.trim().isEmpty()) {
            return;
        }

        BlockPos waypointPos = event.getPos().toImmutable();
        Minecraft.getMinecraft().addScheduledTask(() -> addXaeroWaypoint(waypointPos, name.trim()));
    }

    private static boolean isWaystoneBlock(World world, BlockPos pos) {
        Block block = world.getBlockState(pos).getBlock();
        ResourceLocation registryName = block.getRegistryName();
        return registryName != null
                && WAYSTONES_MOD_ID.equals(registryName.getNamespace())
                && registryName.getPath().contains("waystone");
    }

    private static TileEntity findWaystoneTile(World world, BlockPos pos) {
        TileEntity tile = normalizeWaystoneTile(world.getTileEntity(pos));
        if (tile != null) {
            return tile;
        }

        tile = normalizeWaystoneTile(world.getTileEntity(pos.down()));
        if (tile != null) {
            return tile;
        }

        return normalizeWaystoneTile(world.getTileEntity(pos.up()));
    }

    private static TileEntity normalizeWaystoneTile(TileEntity tile) {
        if (tile == null) {
            return null;
        }

        Object parent = invokeNoArg(tile, "getParent");
        if (parent instanceof TileEntity) {
            tile = (TileEntity) parent;
        }

        return hasNoArgMethod(tile, "getWaystoneName") ? tile : null;
    }

    private static String getWaystoneName(TileEntity tile) {
        Object value = invokeNoArg(tile, "getWaystoneName");
        return value instanceof String ? (String) value : null;
    }

    private static Object invokeNoArg(Object target, String methodName) {
        if (target == null) {
            return null;
        }

        try {
            Method method = target.getClass().getMethod(methodName);
            return method.invoke(target);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
            return null;
        }
    }

    private static boolean hasNoArgMethod(Object target, String methodName) {
        if (target == null) {
            return false;
        }

        try {
            target.getClass().getMethod(methodName);
            return true;
        } catch (NoSuchMethodException ignored) {
            return false;
        }
    }

    private static void addXaeroWaypoint(BlockPos pos, String name) {
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayerSP player = minecraft.player;
        if (player == null || player.connection == null) {
            return;
        }
        if (!(player.connection instanceof IXaeroMinimapClientPlayNetHandler)) {
            return;
        }

        XaeroMinimapSession session = ((IXaeroMinimapClientPlayNetHandler) player.connection).getXaero_minimapSession();
        if (session == null) {
            return;
        }

        WaypointsManager manager = session.getWaypointsManager();
        WaypointWorld currentWorld = manager == null ? null : manager.getCurrentWorld();
        WaypointSet currentSet = currentWorld == null ? null : currentWorld.getCurrentWaypointSet();
        if (currentSet == null) {
            return;
        }

        if (SOFConfig.waystonesToXaero.preventDuplicates && hasMatchingWaypoint(currentSet, pos, name)) {
            return;
        }

        Waypoint waypoint = new Waypoint(
                pos.getX(),
                pos.getY() + 2,
                pos.getZ(),
                name,
                createInitials(name),
                WaypointColor.getRandom()
        );
        currentSet.add(waypoint);

        try {
            manager.getWorldManagerIO().saveWorld((MinimapWorld) currentWorld);
        } catch (IOException error) {
            SOFcore.LOGGER.error("Unable to save automatically generated Xaero's Minimap waypoint.", error);
        }
    }

    private static boolean hasMatchingWaypoint(WaypointSet currentSet, BlockPos pos, String name) {
        for (Waypoint waypoint : currentSet.getWaypoints()) {
            if (name.equals(waypoint.getName())
                    && waypoint.getX() == pos.getX()
                    && waypoint.getY() == pos.getY() + 2
                    && waypoint.getZ() == pos.getZ()) {
                return true;
            }
        }
        return false;
    }

    private static String createInitials(String name) {
        String trimmed = name.trim();
        return trimmed.isEmpty() ? "W" : trimmed.substring(0, 1);
    }
}
