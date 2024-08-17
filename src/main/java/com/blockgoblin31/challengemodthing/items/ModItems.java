package com.blockgoblin31.challengemodthing.items;

import com.blockgoblin31.challengemodthing.ChallengeMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;

public class ModItems {
    public static final DeferredRegister<Item> itemRegister = DeferredRegister.create(Registries.ITEM, ChallengeMod.MODID);
    public static final HashMap<String, RegistryObject<BlockItem>> blockItemMap = new HashMap<>();
    public static final RegistryObject<Item> CURIO_ITEM = itemRegister.register("hi_taylor", () -> new CursedCuriosItem(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        itemRegister.register(eventBus);
    }
}
