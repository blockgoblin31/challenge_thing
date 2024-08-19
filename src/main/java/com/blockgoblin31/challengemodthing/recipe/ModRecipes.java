package com.blockgoblin31.challengemodthing.recipe;

import com.blockgoblin31.challengemodthing.ChallengeMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeType<?>> recipeTypeRegister = DeferredRegister.create(Registries.RECIPE_TYPE, ChallengeMod.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> recipeSerializerRegistry = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ChallengeMod.MODID);

    public static final RegistryObject<RecipeSerializer<ConversionRecipe>> conversionSerializer = recipeSerializerRegistry.register("conversion", () -> ConversionRecipe.ConversionSerializer.instance);
    public static final RegistryObject<RecipeType<ConversionRecipe>> conversionType = recipeTypeRegister.register("conversion", () -> ConversionRecipe.ConversionRecipeType.instance);

    public static void register(IEventBus bus) {
        recipeTypeRegister.register(bus);
        recipeSerializerRegistry.register(bus);
    }
}
