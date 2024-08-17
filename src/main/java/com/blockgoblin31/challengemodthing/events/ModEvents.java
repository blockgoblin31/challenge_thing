package com.blockgoblin31.challengemodthing.events;

import com.blockgoblin31.challengemodthing.ChallengeMod;
import com.blockgoblin31.challengemodthing.commands.DenyCommand;
import com.blockgoblin31.challengemodthing.items.ModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

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
}
