package com.blockgoblin31.challengemodthing.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
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

    @Inject(method="doCloseContainer", at = @At("HEAD"), remap = true)
    private void bg_chal_closeContainer(CallbackInfo ci) {
        if (this.getPersistentData().contains("bg31.deny")) return;
        if (!this.getAdvancements().getOrStartProgress(this.server.getAdvancements().getAdvancement(new ResourceLocation("minecraft:story/mine_stone"))).isDone()) return;
        NonNullList<ItemStack> items = this.getInventory().items;
        ArrayList<ItemStack> greatest = new ArrayList<>();
        greatest.add(items.get(0));
        for (int i = 9; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            if (stack.is(Items.COBBLESTONE)) continue;
            if (!stack.isStackable()) {
                if (greatest.get(0).isStackable()) greatest.clear();
                greatest.add(stack);
                return;
            }
            if (stack.getCount() > greatest.get(0).getCount()) {
                greatest = new ArrayList<>();
                greatest.add(stack);
            } else if (stack.getCount() == greatest.get(0).getCount()) {
                greatest.add(stack);
            }
        }
        ItemStack stack = greatest.get(this.random.nextInt(greatest.size()));
        int index = items.indexOf(stack);
        int size = stack.isStackable() ? stack.getCount() : 64;
        this.getInventory().setItem(index, new ItemStack(Items.COBBLESTONE, size));
    }
}
