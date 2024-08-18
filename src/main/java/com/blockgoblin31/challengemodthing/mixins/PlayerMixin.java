package com.blockgoblin31.challengemodthing.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(ServerPlayer.class)
//@Debug(export = true, print = true)
public abstract class PlayerMixin extends Player {

    @Shadow public abstract PlayerAdvancements getAdvancements();

    @Shadow @Final public MinecraftServer server;

    public PlayerMixin(Level pLevel, BlockPos pPos, float pYRot, GameProfile pGameProfile) {
        super(pLevel, pPos, pYRot, pGameProfile);
    }

    @Inject(method="doCloseContainer", at = @At("HEAD"))
    private void bg_chal_closeContainer(CallbackInfo ci) {
        if (this.getPersistentData().contains("bg31.deny")) return;
        if (!this.getAdvancements().getOrStartProgress(this.server.getAdvancements().getAdvancement(new ResourceLocation("minecraft:story/mine_stone"))).isDone()) return;
        NonNullList<ItemStack> items = this.getInventory().items;
        ArrayList<ItemStack> greatest = new ArrayList<>();
        greatest.add(items.get(9));
        for (int i = 9; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            if (getItemInHand(InteractionHand.MAIN_HAND) == stack || getItemInHand(InteractionHand.OFF_HAND) == stack) continue;
            if (stack.is(Items.COBBLESTONE)) continue;
            if (!stack.isStackable()) {
                if (greatest.get(0).isStackable() || greatest.get(0).is(Items.AIR)) greatest.clear();
                greatest.add(stack);
                continue;
            }
            if (stack.getCount() > greatest.get(0).getCount()) {
                greatest = new ArrayList<>();
                greatest.add(stack);
            } else if (stack.getCount() == greatest.get(0).getCount()) {
                greatest.add(stack);
            }
        }
        ItemStack stack = greatest.get(this.random.nextInt(greatest.size()));
        if (stack.is(Items.AIR)) return;
        int index = items.indexOf(stack);
        int size = stack.isStackable() ? stack.getCount() : stack.getItem() instanceof BlockItem ? 64 : 1;
        this.getInventory().setItem(index, new ItemStack(Items.COBBLESTONE, size));
    }
}
