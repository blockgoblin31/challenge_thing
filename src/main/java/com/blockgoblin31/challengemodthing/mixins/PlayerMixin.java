package com.blockgoblin31.challengemodthing.mixins;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    @Final
    @Shadow
    private Inventory inventory;

    protected PlayerMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method="Lnet/minecraft/world/entity/player/Player;closeContainer()V", at = @At("HEAD"), remap = true)
    @Debug(export = true, print = true)
    private void bg_chal_closeContainer(CallbackInfo ci) {
        NonNullList<ItemStack> items = inventory.items;
        ArrayList<ItemStack> greatest = new ArrayList<>();
        greatest.add(items.get(0));
        for (ItemStack stack : items) {
            if (stack.getCount() > greatest.get(0).getCount()) {
                greatest = new ArrayList<>();
                greatest.add(stack);
            } else if (stack.getCount() == greatest.get(0).getCount()) {
                greatest.add(stack);
            }
        }
        ItemStack stack = greatest.get(this.random.nextInt(greatest.size()));
        stack.setCount((int) Math.ceil(stack.getCount() / 2.0));
    }
}
