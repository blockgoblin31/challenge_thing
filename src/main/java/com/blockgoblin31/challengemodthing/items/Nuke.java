package com.blockgoblin31.challengemodthing.items;

import mekanism.api.Coord4D;
import mekanism.api.radiation.IRadiationManager;
import mekanism.common.lib.radiation.RadiationManager;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class Nuke extends Item {
    public Nuke(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        IRadiationManager.INSTANCE.radiate(new Coord4D(entity), 1000000);
        entity.kill();
        return true;
    }
}
