package com.blockgoblin31.challengemodthing.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.function.BiFunction;

@ParametersAreNonnullByDefault
public class DupeBlock extends AbstractFurnaceBlock {
    static final HashMap<String, RegistryObject<BlockEntityType<DupeBlockEntity>>> beMap = new HashMap<>();
    private final BiFunction<Item, DupeBlockEntity, Item> itemFunc;
    private final String beLocation;
    protected DupeBlock(Properties pProperties, BiFunction<Item, DupeBlockEntity, Item> itemFunc, String beLocation) {
        super(pProperties);
        this.itemFunc = itemFunc;
        this.beLocation = beLocation;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new DupeBlockEntity(beMap.get(beLocation), blockPos, blockState, itemFunc);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof DupeBlockEntity dbe) dbe.dropItems();
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.isClientSide) return InteractionResult.sidedSuccess(true);
        BlockEntity be = pLevel.getBlockEntity(pPos);
        if (!(be instanceof DupeBlockEntity dbe)) throw new IllegalStateException("Incorrect block entity found!");
        dbe.tryToUse(pState, pLevel, pPos, pPlayer, pHand, pHit);
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    protected void openContainer(Level level, BlockPos blockPos, Player player) {
        BlockEntity be = level.getBlockEntity(blockPos);
        if (be instanceof DupeBlockEntity dbe) dbe.openContainer(player, blockPos);
    }

    public BiFunction<Item, DupeBlockEntity, Item> getFunc() {
        return itemFunc;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide) return null;
        return createTickerHelper(pBlockEntityType, beMap.get(this.beLocation).get(), (level, pos, state, blockEntity) -> blockEntity.onTick(level, pos, state));
    }
}
