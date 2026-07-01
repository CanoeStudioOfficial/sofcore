package com.canoestudio.sofcore.compat;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.blocks.BlockRooty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public final class DynamicTreesCompat {

    private DynamicTreesCompat() {
    }

    public static Set<BlockPos> collectExplosionRoots(World world, Iterable<BlockPos> affectedBlocks) {
        Set<BlockPos> roots = new HashSet<>();
        if (world.isRemote) {
            return roots;
        }

        for (BlockPos pos : affectedBlocks) {
            addRootIfPresent(world, pos.toImmutable(), roots);
        }

        return roots;
    }

    public static void destroyRoots(World world, Set<BlockPos> roots) {
        if (world.isRemote || roots.isEmpty()) {
            return;
        }

        for (BlockPos rootPos : roots) {
            IBlockState rootState = world.getBlockState(rootPos);
            if (!(rootState.getBlock() instanceof BlockRooty)) {
                continue;
            }

            world.setBlockToAir(rootPos);
        }
    }

    private static void addRootIfPresent(World world, BlockPos pos, Set<BlockPos> roots) {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof BlockRooty) {
            roots.add(pos);
            return;
        }

        BlockPos rootPos = TreeHelper.findRootNode(world, pos);
        if (rootPos != null && !BlockPos.ORIGIN.equals(rootPos)) {
            IBlockState rootState = world.getBlockState(rootPos);
            if (rootState.getBlock() instanceof BlockRooty) {
                roots.add(rootPos.toImmutable());
            }
        }
    }
}
