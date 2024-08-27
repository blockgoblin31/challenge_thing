package com.blockgoblin31.challengemodthing.recipe;

import com.blockgoblin31.challengemodthing.ChallengeMod;
import com.blockgoblin31.challengemodthing.util.ConditionChecker;
import com.google.gson.JsonObject;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.locks.Condition;
import java.util.function.BiPredicate;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ConversionRecipe implements Recipe<SimpleContainer> {
    private final Ingredient ingredient;
    private final ItemStack output;
    private final ResourceLocation id;
    public static BiPredicate<Level, Object> clientSideTester = (level, smth) -> level.isClientSide;
    BiPredicate<Ingredient, SimpleContainer> ingredientChecker = (ingredient, container) -> ingredient.test(container.getItem(0));

    public ConversionRecipe(ResourceLocation id, ItemStack output, Ingredient ingredient) {
        this.ingredient = ingredient;
        this.output = output;
        this.id = id;
    }

    @Override
    public boolean matches(SimpleContainer simpleContainer, Level level) {
        ConditionChecker checker = new ConditionChecker(clientSideTester, ingredientChecker);
        return (checker.getNext(level, simpleContainer)) ? false : checker.getNext(ingredient, simpleContainer);
    }

    @Override
    public ItemStack assemble(SimpleContainer simpleContainer, RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ConversionSerializer.instance;
    }

    @Override
    public RecipeType<?> getType() {
        return ConversionRecipeType.instance;
    }

    public static class ConversionRecipeType implements RecipeType<ConversionRecipe> {
        public static final ConversionRecipeType instance = new ConversionRecipeType();
        final String id = "conversion";
    }

    public static class ConversionSerializer implements RecipeSerializer<ConversionRecipe> {
        public static final ConversionSerializer instance = new ConversionSerializer();
        public static final ResourceLocation id = new ResourceLocation(ChallengeMod.MODID, "conversion");

        @Override
        public ConversionRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "output"));
            Ingredient input = Ingredient.fromJson(jsonObject.get("input"));
            return new ConversionRecipe(resourceLocation, output, input);
        }

        @Override
        public @Nullable ConversionRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf friendlyByteBuf) {
            Ingredient input = Ingredient.fromNetwork(friendlyByteBuf);
            ItemStack output = friendlyByteBuf.readItem();
            return new ConversionRecipe(resourceLocation, output, input);
        }

        @Override
        public void toNetwork(FriendlyByteBuf friendlyByteBuf, ConversionRecipe conversionRecipe) {
            conversionRecipe.ingredient.toNetwork(friendlyByteBuf);
            friendlyByteBuf.writeItemStack(conversionRecipe.output, false);
        }
    }
}
