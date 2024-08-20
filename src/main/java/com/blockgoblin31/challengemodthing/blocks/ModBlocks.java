package com.blockgoblin31.challengemodthing.blocks;

import com.blockgoblin31.challengemodthing.ChallengeMod;
import com.blockgoblin31.challengemodthing.items.ModItems;
import com.blockgoblin31.challengemodthing.recipe.ConversionRecipe;
import com.hollingsworth.arsnouveau.common.block.tile.CreativeSourceJarTile;
import com.hollingsworth.arsnouveau.common.block.tile.SourceJarTile;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;
import java.util.function.Supplier;

public class ModBlocks {
    static final DeferredRegister<Block> blockRegister = DeferredRegister.create(Registries.BLOCK, "minecraft");
    static final DeferredRegister<BlockEntityType<?>> blockEntityRegister = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ChallengeMod.MODID);
    public static final RegistryObject<Block> dupeBlock = registerBlock("dupe", () -> new DupeBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS), (item, be) -> item, "dupe_be"));
    public static final RegistryObject<BlockEntityType<DupeBlockEntity>> dupeBlockEntity = registerDupeBlockEntity("dupe_be", (pos, state) -> new DupeBlockEntity(pos, state, (item, be) -> item), dupeBlock);
    public static final RegistryObject<Block> conversionBlock = registerBlock("conversion", () -> new DupeBlock(BlockBehaviour.Properties.copy(Blocks.FURNACE), (item, be) -> {
        Optional<ConversionRecipe> recipeOptional = be.getCurrentRecipe();
        return recipeOptional.map(conversionRecipe -> conversionRecipe.getResultItem(null).getItem()).orElse(Items.AIR);
    }, "conversion_be"));
    public static final RegistryObject<BlockEntityType<DupeBlockEntity>> conversionBlockEntity = registerDupeBlockEntity("conversion_be", (pos, state) -> new DupeBlockEntity(pos, state, (item, be) -> {
        Optional<ConversionRecipe> recipeOptional = be.getCurrentRecipe();
        return recipeOptional.map(conversionRecipe -> conversionRecipe.getResultItem(null).getItem()).orElse(Items.AIR);
    }), conversionBlock);
    public static final RegistryObject<Block> sourceJarBlock = registerBlock("source_jar", () -> new BEHolderBlock(BlockBehaviour.Properties.copy(BlockRegistry.SOURCE_JAR.get()), CreativeSourceJarTile::new));

    public static void register(IEventBus eventBus) {
        blockRegister.register(eventBus);
        blockEntityRegister.register(eventBus);
    }

    private static RegistryObject<Block> registerBlock(String name, Supplier<Block> sup) {
        RegistryObject<Block> returnVal = blockRegister.register(name, sup);
        ModItems.blockItemMap.put(name, ModItems.itemRegister.register(name, () -> new BlockItem(returnVal.get(), new Item.Properties())));
        return returnVal;
    }

    private static RegistryObject<BlockEntityType<DupeBlockEntity>> registerDupeBlockEntity(String name, BlockEntityType.BlockEntitySupplier<DupeBlockEntity> beSup, Supplier<Block> blockSup) {
        RegistryObject<BlockEntityType<DupeBlockEntity>> toReturn = blockEntityRegister.register(name, () -> BlockEntityType.Builder.of(beSup, blockSup.get()).build(null));
        DupeBlock.beMap.put(name, toReturn);
        return toReturn;
    }
}
