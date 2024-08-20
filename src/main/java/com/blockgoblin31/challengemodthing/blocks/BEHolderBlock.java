package com.blockgoblin31.challengemodthing.blocks;

import com.hollingsworth.arsnouveau.common.block.tile.SourceJarTile;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public class BEHolderBlock extends BaseEntityBlock {
    final BiFunction<BlockPos, BlockState, BlockEntity> getBlockEntity;

    protected BEHolderBlock(Properties pProperties, BiFunction<BlockPos, BlockState, BlockEntity> getBlockEntity) {
        super(pProperties);
        this.getBlockEntity = getBlockEntity;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return getBlockEntity.apply(blockPos, blockState);
    }
}
