package com.blockgoblin31.challengemodthing.blocks;

import com.blockgoblin31.challengemodthing.ChallengeMod;
import com.blockgoblin31.challengemodthing.items.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    static final DeferredRegister<Block> blockRegister = DeferredRegister.create(Registries.BLOCK, ChallengeMod.MODID);
    static final DeferredRegister<BlockEntityType<?>> blockEntityRegister = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ChallengeMod.MODID);
    public static final RegistryObject<Block> dupeBlock = registerBlock("dupe", () -> new DupeBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<BlockEntityType<DupeBlockEntity>> dupeBlockEntity = blockEntityRegister.register("dupe_be", () -> BlockEntityType.Builder.of(DupeBlockEntity::new, dupeBlock.get()).build(null));

    public static void register(IEventBus eventBus) {
        blockRegister.register(eventBus);
        blockEntityRegister.register(eventBus);
    }

    private static RegistryObject<Block> registerBlock(String name, Supplier<Block> sup) {
        RegistryObject<Block> returnVal = blockRegister.register(name, sup);
        ModItems.blockItemMap.put(name, ModItems.itemRegister.register(name, () -> new BlockItem(returnVal.get(), new Item.Properties())));
        return returnVal;
    }
}
