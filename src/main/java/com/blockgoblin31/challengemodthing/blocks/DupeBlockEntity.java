package com.blockgoblin31.challengemodthing.blocks;

import com.blockgoblin31.challengemodthing.items.ModItems;
import com.blockgoblin31.challengemodthing.recipe.ConversionRecipe;
import com.blockgoblin31.challengemodthing.screen.DupeMenu;
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
import net.minecraft.world.item.Items;
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
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public class DupeBlockEntity extends AbstractFurnaceBlockEntity {
    private final BaseItemHandler handler;
    private final List<String> allowedPlayers = new ArrayList<>();
    private static final int input = 0;
    private static final int output = 1;

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
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
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
        ListTag nbt = new ListTag();
        for (String player : allowedPlayers) {
            nbt.add(StringTag.valueOf(player));
        }
        pTag.put("players", nbt);
        pTag.put("inventory", handler.serializeNBT());
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        handler.deserializeNBT(pTag.getCompound("inventory"));
        ListTag nbt = pTag.getList("players", Tag.TAG_STRING);
        for (int i = 0; i < nbt.size(); i++) {
            allowedPlayers.add(nbt.getString(i));
        }
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
        if (allowedPlayers.contains(player.getUUID().toString())) return new DupeMenu(i, inventory, this);
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
        if (!allowedPlayers.contains(pPlayer.getUUID().toString())) {
            if (pPlayer.getItemInHand(pHand).is(ModItems.CURIO_ITEM.get())) {
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
        for (int i = 0; i < handler.getSlots(); i++) {
            inventory.setItem(i, handler.getStackInSlot(i));
        }
        return this.getLevel().getRecipeManager().getRecipeFor(ConversionRecipe.ConversionRecipeType.instance, inventory, this.level);
    }

    public void updateOutputSlot() {
        handler.updateOutputSlot();
    }

    static class BaseItemHandler extends ItemStackHandler {
        Item currentItem;
        final DupeBlockEntity be;
        final BiFunction<Item, DupeBlockEntity, Item> transformFunction;


        public BaseItemHandler(DupeBlockEntity dbe, int slots, BiFunction<Item, DupeBlockEntity, Item> transformFunction) {
            super(slots);
            this.transformFunction = transformFunction;
            currentItem = getStackInSlot(input).getItem();
            be = dbe;
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (slot == input) {
                currentItem = stack.getItem();
                updateOutputSlot();
            }
            return super.insertItem(slot, stack, simulate);
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack toReturn = super.extractItem(slot, amount, simulate);
            if (slot == input) currentItem = getStackInSlot(input).getItem();
            updateOutputSlot();
            return toReturn;
        }

        public void updateOutputSlot() {
            Item outputItem = transformFunction.apply(currentItem, be);
            if (getStackInSlot(output).is(outputItem)) return;
            setStackInSlot(output, new ItemStack(outputItem, outputItem.getMaxStackSize()));
            be.setChanged();
        }
    }
}
