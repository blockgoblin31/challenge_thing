package com.blockgoblin31.challengemodthing.blocks;

import com.blockgoblin31.challengemodthing.items.ModItems;
import com.blockgoblin31.challengemodthing.screen.DupeMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DupeBlockEntity extends BlockEntity implements MenuProvider {
    private final DupeItemHandler handler = new DupeItemHandler(this, 2);
    private final List<String> allowedPlayers = new ArrayList<>();
    private static final int input = 0;
    private static final int output = 1;

    private LazyOptional<IItemHandler> lazyHandler = LazyOptional.empty();

    public DupeBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlocks.dupeBlockEntity.get(), pPos, pBlockState);
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

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new DupeMenu(i, inventory, this);
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
            return;
        }
        NetworkHooks.openScreen((ServerPlayer) pPlayer, this, pPos);
    }

    static class DupeItemHandler extends ItemStackHandler {
        Item currentItem;
        final DupeBlockEntity be;

        public DupeItemHandler(DupeBlockEntity dbe, int slots) {
            super(slots);
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
            if (getStackInSlot(input).is(Items.AIR)) currentItem = Items.AIR;
            updateOutputSlot();
            return toReturn;
        }

        public void updateOutputSlot() {
            setStackInSlot(output, new ItemStack(currentItem, currentItem.getMaxStackSize()));
            be.setChanged();
        }
    }
}
