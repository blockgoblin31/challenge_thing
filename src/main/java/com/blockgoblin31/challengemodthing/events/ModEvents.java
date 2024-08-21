package com.blockgoblin31.challengemodthing.events;

import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.natives.entity.type.player.ExpandPlayer;
import com.blockgoblin31.challengemodthing.ChallengeMod;
import com.blockgoblin31.challengemodthing.commands.DenyCommand;
import com.blockgoblin31.challengemodthing.items.ModItems;
import net.minecraft.nbt.ByteTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

@Mod.EventBusSubscriber(modid = ChallengeMod.MODID)
public class ModEvents {

    @SubscribeEvent
    static void listen(RegisterCommandsEvent e) {
        DenyCommand.register(e.getDispatcher());
    }

    @SubscribeEvent
    static void listen(PlayerEvent.PlayerLoggedInEvent e) {
        Player player = e.getEntity();
        if (player.level().isClientSide) return;
        LazyOptional<ICuriosItemHandler> inventory = CuriosApi.getCuriosInventory(e.getEntity());
        inventory.ifPresent((handler) -> {
            if (!handler.getStacksHandler("ring").orElseThrow().getStacks().getStackInSlot(1).is(Items.AIR)) return;
            handler.setEquippedCurio("ring", 1, new ItemStack(ModItems.CURIO_ITEM.get(), 1));
        });
    }

    @SubscribeEvent
    static void listen(PlayerEvent.PlayerRespawnEvent e) {
        if (e.getEntity().level().isClientSide) return;
        if (!e.getEntity().getPersistentData().contains("bg31.hasRing") || e.getEntity().getPersistentData().getBoolean("bg31.hasRing")) {
            ICurioStacksHandler handler = CuriosApi.getCuriosInventory(e.getEntity()).orElseThrow(() -> new IllegalStateException("Player " + e.getEntity().getName() + " has no curios inventory!")).getStacksHandler("ring").orElseThrow();
            handler.getStacks().setStackInSlot(1, new ItemStack(ModItems.CURIO_ITEM.get()));
        }
    }

    @SubscribeEvent
    static void listen(LivingDeathEvent e) {
        if (e.getEntity().level().isClientSide ||!(e.getEntity() instanceof ServerPlayer player)) return;
        ICuriosItemHandler handler = CuriosApi.getCuriosInventory(player).orElseThrow(() -> new IllegalStateException("Player " + player.getName() + " has no curios inventory!"));
        if (handler.getStacksHandler("ring").orElseThrow().getStacks().getStackInSlot(1).is(ModItems.CURIO_ITEM.get())) {
            player.getPersistentData().put("bg31.hasRing", ByteTag.valueOf(true));
            handler.getStacksHandler("ring").orElseThrow().getStacks().setStackInSlot(1, Items.AIR.getDefaultInstance());
        } else {
            player.getPersistentData().put("bg31.hasRing", ByteTag.valueOf(false));
        }
    }

    @SubscribeEvent
    static void listen(PlayerInteractEvent.RightClickBlock e) {
        Player player = e.getEntity();
        if (player.level().isClientSide) return;
        if (!player.getItemInHand(e.getHand()).is(ModItems.SCANNER.get())) return;
        ItemStack item = player.level().getBlockState(e.getPos()).getBlock().asItem().getDefaultInstance().copy();
        if (e.getLevel().getBlockEntity(e.getPos()) != null) {
            e.getLevel().getBlockEntity(e.getPos()).saveToItem(item);
        }
        if (player.isShiftKeyDown()) item.setCount(item.getMaxStackSize());
        player.addItem(item);
    }
}
