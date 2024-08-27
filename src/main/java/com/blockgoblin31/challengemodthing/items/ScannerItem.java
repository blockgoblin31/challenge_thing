package com.blockgoblin31.challengemodthing.items;

import mekanism.api.math.FloatingLong;
import mekanism.common.capabilities.energy.BasicEnergyContainer;
import mekanism.common.item.ItemEnergized;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ScannerItem extends ItemEnergized {

    public ScannerItem(Properties properties) {
        super(() -> FloatingLong.create(1), () -> FloatingLong.create(1000000000000000L), BasicEnergyContainer.alwaysTrue, BasicEnergyContainer.alwaysFalse, properties);
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return false;
    }
}
