package com.blockgoblin31.challengemodthing.screen;

import com.blockgoblin31.challengemodthing.blocks.DupeBlockEntity;
import com.blockgoblin31.challengemodthing.blocks.ModBlocks;
import com.blockgoblin31.challengemodthing.util.FunctionPasser;
import com.blockgoblin31.challengemodthing.util.IterationHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

import java.util.ArrayList;

public class DupeMenu extends AbstractContainerMenu {
    public final DupeBlockEntity be;
    private final Level level;

    public DupeMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public DupeMenu(int containerId, Inventory inv, BlockEntity entity) {
        super(ModMenuTypes.dupeMenu.get(), containerId);
        checkContainerSize(inv, 2);
        be = (DupeBlockEntity) entity;
        level = inv.player.level();
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        this.be.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent((handler) -> {
            addSlot(new SlotItemHandler(handler, 0, 80, 11));
            addSlot(new SlotItemHandler(handler, 1, 80, 59));
        });
    }

    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = 2;  // must be the number of slots you have!
    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + pIndex);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, be.getBlockPos()), player, be.getBlockState().getBlock());
    }

    private void addPlayerInventory(Inventory inv) {
        int[] i = {0};
        IterationHelper.WhileLoop<Slot> slotLoop = IterationHelper.whileLoop(() -> i[0] < 3, new FunctionPasser<Slot>() {
            @Override
            public Slot get(Slot input) {
                return input;
            }

            @Override
            public ArrayList<Slot> getFinal(ArrayList<Slot> input) {
                return input;
            }

            @Override
            public void process() {
                int[] j = {0};
                IterationHelper.WhileLoop<Slot> internalSlotLoop = IterationHelper.whileLoop(() -> j[0] < 3, new FunctionPasser<Slot>() {
                    @Override
                    public Slot get(Slot input) {
                        return input;
                    }

                    @Override
                    public ArrayList<Slot> getFinal(ArrayList<Slot> input) {
                        return input;
                    }

                    @Override
                    public void process() {
                        addSlot(new Slot(inv, j[0] + i[0] * 9 + 9, 8 + j[0] * 18, 84 + i[0] * 18));
                        j[0] = j[0] + 1;
                    }
                });
                i[0] = i[0] + 1;
            }
        });
        slotLoop.loopThrough();
    }

    private void addPlayerHotbar(Inventory inv) {
        int[] i = {0};
        IterationHelper.WhileLoop<Slot> slotLoop = IterationHelper.whileLoop(() -> i[0] < 9, new FunctionPasser<Slot>() {
            @Override
            public Slot get(Slot input) {
                return input;
            }

            @Override
            public ArrayList<Slot> getFinal(ArrayList<Slot> input) {
                return input;
            }

            @Override
            public void process() {
                addSlot(new Slot(inv, i[0], 8 + i[0] * 18, 142));
                i[0] = i[0] + 1;
            }
        });
    }
}
