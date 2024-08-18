package com.blockgoblin31.challengemodthing.items;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

public class CursedCuriosItem extends Item {

    public CursedCuriosItem(Properties pProperties) {
        super(pProperties.stacksTo(1));
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundTag unused) {
        return CuriosApi.createCurioProvider(new ICurio() {
            int delay = 6000;
            //int delay = 600;

            @Override
            public ItemStack getStack() {
                return stack;
            }

            @Override
            public void curioTick(SlotContext slotContext) {
                LivingEntity entity = slotContext.entity();
                if (!(entity instanceof Player player)) return;
                if ((player.level().isClientSide)) return;
                ServerLevel level = (ServerLevel) player.level();
                if (delay != 0) {
                    delay--;
                    return;
                }
                Vec3 angle = player.getLookAngle();
                BlockPos playerPos = player.getOnPos();
                BlockPos position = new BlockPos(angle.x < 0 ? playerPos.getX() + 1 : playerPos.getX() - 1, playerPos.getY(), angle.z < 0 ? playerPos.getZ() + 1 : playerPos.getZ() - 1);
                if (level.getBlockState(position).isSolid() || level.getBlockState(position.above()).isSolid()) {
                    return;
                }
                Creeper creeper = new Creeper(EntityType.CREEPER, level);
                creeper.getEntityData().set(Creeper.DATA_IS_POWERED, true);
                creeper.moveTo(position, 0, 0);
                level.addFreshEntity(creeper);
                creeper.ignite();
                delay = level.random.nextIntBetweenInclusive(3600, 8400);
                //delay = 100;
            }
        });
    }
}
