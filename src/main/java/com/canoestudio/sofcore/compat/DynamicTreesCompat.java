package com.canoestudio.sofcore.compat;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.blocks.BlockRooty;
import com.ferreusveritas.dynamictrees.trees.Species;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class DynamicTreesCompat {

    private DynamicTreesCompat() {
    }

    public static Set<RootDecay> collectExplosionRoots(World world, Iterable<BlockPos> affectedBlocks) {
        Set<RootDecay> roots = new HashSet<>();
        if (world.isRemote) {
            return roots;
        }

        for (BlockPos pos : affectedBlocks) {
            addRootIfPresent(world, pos.toImmutable(), roots);
        }

        return roots;
    }

    public static void decayRoots(World world, Set<RootDecay> roots) {
        if (world.isRemote || roots.isEmpty()) {
            return;
        }

        for (RootDecay root : roots) {
            IBlockState rootState = world.getBlockState(root.rootPos);
            if (!(rootState.getBlock() instanceof BlockRooty)) {
                continue;
            }

            ((BlockRooty) rootState.getBlock()).doDecay(world, root.rootPos, rootState, root.species);
        }
    }

    private static void addRootIfPresent(World world, BlockPos pos, Set<RootDecay> roots) {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof BlockRooty) {
            roots.add(createRootDecay(world, pos, state));
            return;
        }

        BlockPos rootPos = TreeHelper.findRootNode(world, pos);
        if (rootPos != null && !BlockPos.ORIGIN.equals(rootPos)) {
            IBlockState rootState = world.getBlockState(rootPos);
            if (rootState.getBlock() instanceof BlockRooty) {
                roots.add(createRootDecay(world, rootPos.toImmutable(), rootState));
            }
        }
    }

    private static RootDecay createRootDecay(World world, BlockPos rootPos, IBlockState rootState) {
        BlockRooty rootyBlock = (BlockRooty) rootState.getBlock();
        Species species = rootyBlock.getSpecies(rootState, world, rootPos);
        if (species == null) {
            species = Species.NULLSPECIES;
        }

        return new RootDecay(rootPos, species);
    }

    public static final class RootDecay {

        private final BlockPos rootPos;
        private final Species species;

        private RootDecay(BlockPos rootPos, Species species) {
            this.rootPos = rootPos;
            this.species = species;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof RootDecay && rootPos.equals(((RootDecay) obj).rootPos);
        }

        @Override
        public int hashCode() {
            return Objects.hash(rootPos);
        }
    }
}
