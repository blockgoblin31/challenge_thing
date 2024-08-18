package com.blockgoblin31.challengemodthing;

import com.blockgoblin31.challengemodthing.blocks.ModBlocks;
import com.blockgoblin31.challengemodthing.items.ModItems;
import com.blockgoblin31.challengemodthing.screen.ModMenuTypes;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ChallengeMod.MODID)
public class ChallengeMod {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "bg_chal";

    public ChallengeMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModMenuTypes.register(modEventBus);
    }
}
