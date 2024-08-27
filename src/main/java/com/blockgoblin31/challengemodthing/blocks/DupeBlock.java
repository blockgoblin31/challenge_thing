package com.blockgoblin31.challengemodthing.blocks;

import com.blockgoblin31.challengemodthing.recipe.ConversionRecipe;
import com.blockgoblin31.challengemodthing.util.ConditionChecker;
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
import java.util.function.BiPredicate;

@ParametersAreNonnullByDefault
public class DupeBlock extends AbstractFurnaceBlock {
    static final HashMap<String, RegistryObject<BlockEntityType<DupeBlockEntity>>> beMap = new HashMap<>();
    private final BiFunction<Item, DupeBlockEntity, Item> itemFunc;
    private final String beLocation;
    BiPredicate<BlockState, BlockState> equals = (state, newState) -> state.getBlock().equals(newState.getBlock());
    BiPredicate<BlockEntity, Level> isDbe = (entity, obj) -> {
        if (entity instanceof DupeBlockEntity dbe) dbe.dropItems();
        return entity instanceof DupeBlockEntity;
    };
    BiPredicate<BlockEntity, Level> isDbeReal = (entity, obj) -> entity instanceof DupeBlockEntity;

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
        ConditionChecker checker = new ConditionChecker(equals, isDbe);
        if (checker.getNext(pState, pNewState)) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            checker.getNext(blockEntity, pLevel);
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ConditionChecker checker = new ConditionChecker(ConversionRecipe.clientSideTester, isDbeReal);
        if (checker.getNext(pLevel, pPlayer)) return InteractionResult.sidedSuccess(true);
        BlockEntity be = pLevel.getBlockEntity(pPos);
        if (!checker.getNext(be, pLevel)) throw new IllegalStateException("Incorrect block entity found!");
        DupeBlockEntity dbe = (DupeBlockEntity) be;
        dbe.tryToUse(pState, pLevel, pPos, pPlayer, pHand, pHit);
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    protected void openContainer(Level level, BlockPos blockPos, Player player) {
        ConditionChecker checker = new ConditionChecker(isDbeReal);
        BlockEntity be = level.getBlockEntity(blockPos);
        if (checker.getNext(be, level)) {
            DupeBlockEntity dbe = (DupeBlockEntity) be;
            dbe.openContainer(player, blockPos);
        }
    }

    public BiFunction<Item, DupeBlockEntity, Item> getFunc() {
        return itemFunc;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        ConditionChecker checker = new ConditionChecker(ConversionRecipe.clientSideTester);
        if (checker.getNext(pLevel, pBlockEntityType)) return null;
        return createTickerHelper(pBlockEntityType, beMap.get(this.beLocation).get(), (level, pos, state, blockEntity) -> blockEntity.onTick(level, pos, state));
    }
}
