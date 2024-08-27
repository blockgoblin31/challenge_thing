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
    public static final RegistryObject<Item> ANGEL_RING = itemRegister.register("angel_ring", AngelRingItem::new);
    public static final RegistryObject<Item> BLACKEST_LOTUS = itemRegister.register("blackest_lotus", () -> new BlackestLotusItem(new Item.Properties()));
    public static final RegistryObject<Item> SCANNER = itemRegister.register("scanner", () -> new ScannerItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SUPER_RING = itemRegister.register("why_bother", () -> new SuperRing(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> NUKE = itemRegister.register("oopsie", () -> new Nuke(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> WAND = itemRegister.register("dim", () -> new WandItem(new Item.Properties()));


    public static void register(IEventBus eventBus) {
        itemRegister.register(eventBus);
    }
}
