package com.blockgoblin31.challengemodthing.util;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.util.function.BiPredicate;

public interface ResetableFunctionPasser<T> extends FunctionPasser<T> {
    BiPredicate<BiStorage<Player, InteractionHand>, Item> isHolding = (player, item) -> player.getF().getItemInHand(player.getS()).is(item);
    void reset();
}
