package com.blockgoblin31.challengemodthing.blocks;

import com.blockgoblin31.challengemodthing.ChallengeMod;
import com.blockgoblin31.challengemodthing.items.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    static final DeferredRegister<Block> blockRegister = DeferredRegister.create(Registries.BLOCK, ChallengeMod.MODID);

    public static void register(IEventBus eventBus) {
        blockRegister.register(eventBus);
    }

    private static RegistryObject<Block> registerBlock(String name, Supplier<Block> sup) {
        RegistryObject<Block> returnVal = blockRegister.register(name, sup);
        ModItems.blockItemMap.put(name, ModItems.itemRegister.register(name, () -> new BlockItem(returnVal.get(), new Item.Properties())));
        return returnVal;
    }
}
