package com.blockgoblin31.challengemodthing.mixins;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.CommandSuggestions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(CommandSuggestions.SuggestionsList.class)
public class CommandMixin {

    @Inject(method = "<init>", at=@At("TAIL"), remap = true)
    private void bg_chal_suggestionList(CommandSuggestions p_93956_, int pXPos, int pYPos, int pWidth, List<Suggestion> pSuggestionList, boolean pNarrateFirstSuggestion, CallbackInfo ci) {
        pSuggestionList.removeIf((s) -> {
            return s.getText().equals("deny");
        });
    }
}
