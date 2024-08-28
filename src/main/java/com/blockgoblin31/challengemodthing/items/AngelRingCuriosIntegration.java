package com.blockgoblin31.challengemodthing.items;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.server.ServerLifecycleHooks;
import top.theillusivec4.curios.api.*;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiPredicate;

public class AngelRingCuriosIntegration {
    private static int ticksDrained;
    public static boolean once = true;
    public static BiPredicate<List<String>, String> containsPred = (list, string) -> list.contains(string);

    public static void sendImc() {
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("angelring").icon(new ResourceLocation("curios:slot/empty_ring_slot")).build());
    }

    public static ICapabilityProvider initCapabilities() {
        ICurio curio = new AbstractRingCurio(ModItems.ANGEL_RING.get().asItem()) {
            final ItemStack stack = new ItemStack(ModItems.ANGEL_RING.get()); // I believe it should work since there is no additional data by default.

            @Override
            public ItemStack getStack() {
                return stack;
            }

            @Override
            protected boolean checkIfAllowedToFly(Player player, ItemStack stack) {
                return true;
            }

            @Override
            protected Component getNotAbleToFlyMessage() {
                return Component.translatable("item.angelring.angel_ring.not_enough_xp");
            }

            @Override
            protected void payForFlight(Player player, ItemStack stack) {

            }

            @Override
            protected boolean warnPlayer(Player player, ItemStack stack) {
                return false;
            }
        };

        return new ICapabilityProvider() {
            private final LazyOptional<ICurio> curioOpt = LazyOptional.of(() -> curio);

            @Nonnull
            @Override
            public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap,
                                                     @Nullable Direction side) {

                return CuriosCapability.ITEM.orEmpty(cap, curioOpt);
            }
        };
    }

    public static ServerPlayer getServerPlayerInstance(UUID playerUUID) {
        ServerPlayer player = null;

        if (ServerLifecycleHooks.getCurrentServer() != null) {
            player = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayer(playerUUID);
        }

        return player;
    }

    public static abstract class AbstractRingCurio implements ICurio {
        private static final ClientboundSetActionBarTextPacket packet = new ClientboundSetActionBarTextPacket(Component.translatable("angelring.warning"));
        private final Item item;
        public AbstractRingCurio(Item item) {
            this.item = item;  // I have the suspicion that this results in a circular reference but yolo.
        }

        @Override
        public boolean canEquipFromUse(SlotContext slotContext) {
            return true;
        }

        @Override
        public void onUnequip(SlotContext slotContext, ItemStack newStack) {
            if (newStack.getCapability(ForgeCapabilities.ENERGY).isPresent() && item.getDefaultInstance().getCapability(ForgeCapabilities.ENERGY).isPresent()) {
                if (newStack.getCapability(ForgeCapabilities.ENERGY).resolve().get().getEnergyStored() ==
                        item.getDefaultInstance().getCapability(ForgeCapabilities.ENERGY).resolve().get().getEnergyStored()) return;
            }

            LivingEntity livingEntity = slotContext.entity();
            if (livingEntity instanceof Player) {
                Player player = (Player) livingEntity;

                if (player.isCreative() || player.isSpectator()) return;

                stopFlying(player);
            }
        }

        private void startFlying(Player player) {
            player.getAbilities().mayfly = true;
            player.onUpdateAbilities();
        }

        private void stopFlying(Player player) {
            player.getAbilities().flying = false;
            player.getAbilities().mayfly = false;
            player.onUpdateAbilities();
        }

        @Override
        public void onEquipFromUse(SlotContext slotContext) {
            slotContext.entity().playSound(SoundEvents.ARMOR_EQUIP_ELYTRA,
                    1.0F, 1.0F);
        }

        abstract protected boolean checkIfAllowedToFly(Player player, ItemStack stack);
        abstract protected Component getNotAbleToFlyMessage();
        abstract protected void payForFlight(Player player, ItemStack stack);
        abstract protected boolean warnPlayer(Player player, ItemStack stack);

        @Override
        public void curioTick(SlotContext slotContext) {
            Optional<SlotResult> optStack = CuriosApi.getCuriosHelper().findFirstCurio(slotContext.entity(), item);

            if (optStack.isEmpty()) return;

            ItemStack stack = optStack.get().stack().getCraftingRemainingItem();

            if (slotContext.entity() instanceof Player) {
                Player player = ((Player) slotContext.entity());

                if (player.isCreative() || player.isSpectator()) return;

                if (checkIfAllowedToFly(player, stack) && !player.getAbilities().mayfly) {
                    startFlying(player);
                } else if (!checkIfAllowedToFly(player, stack) && player.getAbilities().mayfly) {
                    stopFlying(player);
                    if (player instanceof ServerPlayer) {
                        player.sendSystemMessage(getNotAbleToFlyMessage());
                    }
                }

                if (player.getAbilities().mayfly && player.getAbilities().flying) {
                    AngelRingCuriosIntegration.once = true;
                    payForFlight(player, stack);

                    if (player instanceof ServerPlayer serverPlayer && warnPlayer(player, stack)) serverPlayer.connection.send(packet);
                }
            }
        }
    }
}