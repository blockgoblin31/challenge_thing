package com.blockgoblin31.challengemodthing.blocks;

import com.blockgoblin31.challengemodthing.ChallengeMod;
import com.blockgoblin31.challengemodthing.items.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    static final DeferredRegister<Block> blockRegister = DeferredRegister.create(Registries.BLOCK, ChallengeMod.MODID);
    static final RegistryObject<Block> blank = blockRegister.register("blank", () -> new Block(BlockBehaviour.Properties.of()));

    public static void register(IEventBus eventBus) {
        blockRegister.register(eventBus);
    }

    private static RegistryObject<Block> registerBlock(String name, BlockBehaviour.Properties props) {
        Block b = new Block(props);
        ModItems.blockItemMap.put(name, ModItems.itemRegister.register(name, () -> new BlockItem(b, new Item.Properties())));
        return blockRegister.register(name, () -> b);
    }
}
