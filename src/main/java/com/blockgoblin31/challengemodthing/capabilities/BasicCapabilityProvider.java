package com.blockgoblin31.challengemodthing.capabilities;

import mekanism.api.radiation.capability.IRadiationShielding;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICurio;

public class BasicCapabilityProvider implements ICapabilityProvider {
    private final ICurio curio;
    private final IRadiationShielding shielding;

    public BasicCapabilityProvider(ICurio curio, IRadiationShielding shielding) {
        this.curio = curio;
        this.shielding = shielding;
    }


    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CuriosCapability.ITEM) return (LazyOptional<T>) LazyOptional.of(() -> curio);
        if (cap == Capabilities.RADIATION_SHIELDING) return (LazyOptional<T>) LazyOptional.of(() -> shielding);
        return LazyOptional.empty();
    }
}
