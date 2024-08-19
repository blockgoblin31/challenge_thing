package com.blockgoblin31.challengemodthing.items;

import com.blockgoblin31.challengemodthing.ChallengeMod;
import dev.denismasterherobrine.angelring.compat.curios.integration.ClassicAngelRingIntegration;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;

import javax.annotation.Nullable;

import java.util.List;

@Mod.EventBusSubscriber(modid = ChallengeMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AngelRingItem extends Item {
    public AngelRingItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @SubscribeEvent
    public static void sendImc(InterModEnqueueEvent event) {
        AngelRingCuriosIntegration.sendImc();
    }

    @Override
    public ICapabilityProvider initCapabilities(final ItemStack stack, CompoundTag unused) {
        return AngelRingCuriosIntegration.initCapabilities();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level world, List<Component> tooltip, TooltipFlag tooltipFlag) {
        if (!Screen.hasShiftDown())
            tooltip.add(Component.translatable("item.angelring.angel_ring.tooltip").withStyle(ChatFormatting.GRAY));
        if (Screen.hasShiftDown()){
            tooltip.add(Component.translatable("item.angelring.angel_ring.classic.desc0").withStyle(ChatFormatting.RED));
        }

        tooltip.add(Component.translatable("item.angelring.tooltip.base").withStyle(ChatFormatting.DARK_GREEN));
    }
}