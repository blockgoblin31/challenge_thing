package com.blockgoblin31.challengemodthing.items;

import com.blockgoblin31.challengemodthing.capabilities.BasicCapabilityProvider;
import com.blockgoblin31.challengemodthing.capabilities.CustomCapabilityProvider;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import mekanism.api.radiation.capability.IRadiationShielding;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.UUID;

public class SuperRing extends Item {
    public SuperRing(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        ICurio curio = new ICurio() {
            private static final AttributeModifier modifier = new AttributeModifier("add_lots", 8, AttributeModifier.Operation.MULTIPLY_TOTAL);

            @Override
            public ItemStack getStack() {
                return stack;
            }

            @Override
            public void curioTick(SlotContext slotContext) {
                LivingEntity entity = slotContext.entity();
                if (!(entity instanceof Player player)) return;
                if (player.level().isClientSide) return;
                player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 200, 1, false, true));
            }

            @Override
            public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid) {
                Multimap<Attribute,AttributeModifier> map = LinkedHashMultimap.create();
                map.put(Attributes.MAX_HEALTH, modifier);
                return map;
            }
        };
        IRadiationShielding shielding = () -> 1;
        //CustomCapabilityProvider<ICurio, IRadiationShielding> provider = new CustomCapabilityProvider<>((cap) -> CuriosCapability.ITEM.orEmpty(cap, LazyOptional.of(() -> curio)), (cap) -> CapabilityManager.get(new CapabilityToken<IRadiationShielding>() {}).orEmpty(cap, LazyOptional.of(() -> shielding)));
        BasicCapabilityProvider provider = new BasicCapabilityProvider(curio, shielding);
        return provider;
    }
}
