package com.blockgoblin31.challengemodthing.mixins;

import com.mojang.brigadier.suggestion.Suggestion;
import net.minecraft.client.gui.components.CommandSuggestions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(CommandSuggestions.SuggestionsList.class)
public class CommandMixin {

    @Inject(method = "<init>", at=@At("TAIL"))
    private void bg_chal_suggestionList(CommandSuggestions p_93956_, int pXPos, int pYPos, int pWidth, List<Suggestion> pSuggestionList, boolean pNarrateFirstSuggestion, CallbackInfo ci) {
        pSuggestionList.removeIf((s) -> s.getText().equals("deny"));
    }
}
