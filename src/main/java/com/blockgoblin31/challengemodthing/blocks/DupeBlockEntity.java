package com.blockgoblin31.challengemodthing.blocks;

import com.blockgoblin31.challengemodthing.compat.crafttweaker.recipes.ConversionRecipeManager;
import com.blockgoblin31.challengemodthing.items.AngelRingCuriosIntegration;
import com.blockgoblin31.challengemodthing.items.ModItems;
import com.blockgoblin31.challengemodthing.recipe.ConversionRecipe;
import com.blockgoblin31.challengemodthing.screen.DupeMenu;
import com.blockgoblin31.challengemodthing.util.*;
import com.blockgoblin31.myLib.BiStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.locks.Condition;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

public class DupeBlockEntity extends AbstractFurnaceBlockEntity {
    private final BaseItemHandler handler;
    private final ArrayList<String> allowedPlayers = new ArrayList<>();
    private static final int input = 0;
    private static final int output = 1;
    BiPredicate<Capability<?>, Direction> capPred = (cap, side) -> cap == ForgeCapabilities.ITEM_HANDLER;

    private LazyOptional<IItemHandler> lazyHandler = LazyOptional.empty();

    public DupeBlockEntity(BlockPos pPos, BlockState pBlockState, BiFunction<Item, DupeBlockEntity, Item> func) {
        this(ModBlocks.conversionBlockEntity, pPos, pBlockState, func);
    }

    public DupeBlockEntity(RegistryObject<BlockEntityType<DupeBlockEntity>> beType, BlockPos pPos, BlockState pBlockState, BiFunction<Item, DupeBlockEntity, Item> func) {
        super(beType.get(), pPos, pBlockState, RecipeType.SMELTING);
        this.handler = new BaseItemHandler(this, 2, func);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        ConditionChecker checker = new ConditionChecker(capPred);
        if (checker.getNext(cap, side)) {
            return lazyHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyHandler = LazyOptional.of(() -> handler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyHandler.invalidate();
    }

    public void dropItems() {
        SimpleContainer inv = new SimpleContainer(1);
        inv.addItem(handler.getStackInSlot(input));
        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        OutputHandlingFunctionPasser<String, ListTag> func = new OutputHandlingFunctionPasser<>() {
            ListTag output = new ListTag();

            @Override
            public ListTag getOutput() {
                return output;
            }

            @Override
            public String get(String input) {
                output.add(StringTag.valueOf(input));
                return input;
            }

            @Override
            public ArrayList<String> getFinal(ArrayList<String> input) {
                return input;
            }

            @Override
            public void process() {

            }
        };
        IterationHelper.ForLoop<String> loop = new IterationHelper.ForLoop<>(func);
        loop.loop(allowedPlayers);
        pTag.put("players", func.getOutput());
        pTag.put("inventory", handler.serializeNBT());
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        handler.deserializeNBT(pTag.getCompound("inventory"));
        IterationHelper.ForLoop<String> loop = IterationHelper.forLoop(new FunctionPasser<>() {
            @Override
            public String get(String input) {
                allowedPlayers.add(input);
                return input;
            }

            @Override
            public ArrayList<String> getFinal(ArrayList<String> input) {
                return input;
            }

            @Override
            public void process() {

            }
        });
        ListTag nbt = pTag.getList("players", Tag.TAG_STRING);
        loop.loop((ArrayList<String>) nbt.stream().map(Tag::getAsString).toList());
        super.load(pTag);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.bg_chal.dupe");
    }

    @Override
    protected Component getDefaultName() {
        return Component.literal("Furnace");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        ConditionChecker checker = new ConditionChecker(AngelRingCuriosIntegration.containsPred);
        if (checker.getNext(allowedPlayers, player.getUUID().toString())) return new DupeMenu(i, inventory, this);
        return new FurnaceMenu(i, inventory);
    }

    @Override
    protected AbstractContainerMenu createMenu(int i, Inventory inventory) {
        return new FurnaceMenu(i, inventory);
    }

    public void onTick(Level level, BlockPos pos, BlockState state) {
        handler.updateOutputSlot();
    }

    public void tryToUse(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ConditionChecker checker = new ConditionChecker(AngelRingCuriosIntegration.containsPred, ResetableFunctionPasser.isHolding);
        if (!checker.getNext(allowedPlayers, pPlayer.getUUID().toString())) {
            if (checker.getNext(new BiStorage(pPlayer, pHand), ModItems.CURIO_ITEM.get())) {
                allowedPlayers.add(pPlayer.getUUID().toString());
                setChanged();
            }
        }
    }

    public void openContainer(Player player, BlockPos blockPos) {
        NetworkHooks.openScreen((ServerPlayer) player, this, blockPos);
    }

    Optional<ConversionRecipe> getCurrentRecipe() {
        SimpleContainer inventory = new SimpleContainer(handler.getSlots());
        OutputHandlingFunctionPasser<ItemStack, Integer> func = new OutputHandlingFunctionPasser<ItemStack, Integer>() {
            int index = 0;
            @Override
            public Integer getOutput() {
                return index;
            }

            @Override
            public ItemStack get(ItemStack input) {
                return input;
            }

            @Override
            public ArrayList<ItemStack> getFinal(ArrayList<ItemStack> input) {
                return input;
            }

            @Override
            public void process() {
                inventory.setItem(index, handler.getStackInSlot(index));
                index++;
            }
        };
        IterationHelper.WhileLoop<ItemStack> loop = IterationHelper.whileLoop(() -> func.getOutput() < handler.getSlots(), func);
        loop.loopThrough();
        return this.getLevel().getRecipeManager().getRecipeFor(ConversionRecipe.ConversionRecipeType.instance, inventory, this.level);
    }

    public void updateOutputSlot() {
        handler.updateOutputSlot();
    }

    static class BaseItemHandler extends ItemStackHandler {
        Item currentItem;
        final DupeBlockEntity be;
        final BiFunction<Item, DupeBlockEntity, Item> transformFunction;
        Supplier<ConditionChecker> getCondChecker = () -> new ConditionChecker(ConversionRecipeManager.predicates);


        public BaseItemHandler(DupeBlockEntity dbe, int slots, BiFunction<Item, DupeBlockEntity, Item> transformFunction) {
            super(slots);
            this.transformFunction = transformFunction;
            currentItem = getStackInSlot(input).getItem();
            be = dbe;
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (getCondChecker.get().getNext(slot, input)) {
                currentItem = stack.getItem();
                updateOutputSlot();
            }
            return super.insertItem(slot, stack, simulate);
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack toReturn = super.extractItem(slot, amount, simulate);
            if (getCondChecker.get().getNext(slot, input)) currentItem = getStackInSlot(input).getItem();
            updateOutputSlot();
            return toReturn;
        }

        public void updateOutputSlot() {
            Item outputItem = transformFunction.apply(currentItem, be);
            if (getCondChecker.get().getNext(getStackInSlot(output).getItem(), outputItem)) return;
            setStackInSlot(output, new ItemStack(outputItem, outputItem.getMaxStackSize()));
            be.setChanged();
        }
    }
}
