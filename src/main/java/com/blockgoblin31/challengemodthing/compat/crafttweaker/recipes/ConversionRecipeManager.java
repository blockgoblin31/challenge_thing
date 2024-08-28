package com.blockgoblin31.challengemodthing.compat.crafttweaker.recipes;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.action.recipe.ActionAddRecipe;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker.api.ingredient.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.recipe.manager.base.IRecipeManager;
import com.blockgoblin31.challengemodthing.recipe.ConversionRecipe;
import com.blockgoblin31.challengemodthing.screen.ModMenuTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import org.openzen.zencode.java.ZenCodeType;

import java.util.function.BiPredicate;

@ZenRegister
@ZenCodeType.Name("mods.bg_chal.recipe.manager.ConversionRecipeManager")
public class ConversionRecipeManager implements IRecipeManager<ConversionRecipe> {
    public static BiPredicate[] predicates = new BiPredicate[]{ModMenuTypes.equals};
    @Override
    public RecipeType<ConversionRecipe> getRecipeType() {
        return ConversionRecipe.ConversionRecipeType.instance;
    }

    @ZenCodeType.Method
    public void addRecipe(String name, IItemStack output, IIngredient input) {
        ConversionRecipe recipe = new ConversionRecipe(new ResourceLocation("crafttweaker", name), output.getInternal(), input.asVanillaIngredient());
        CraftTweakerAPI.apply(new ActionAddRecipe<>(this, recipe));
    }
}
