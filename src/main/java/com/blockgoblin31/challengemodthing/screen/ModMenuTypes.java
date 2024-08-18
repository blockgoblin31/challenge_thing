package com.blockgoblin31.challengemodthing.screen;

import com.blockgoblin31.challengemodthing.ChallengeMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> menuTypeRegister = DeferredRegister.create(Registries.MENU, ChallengeMod.MODID);

    public static final RegistryObject<MenuType<DupeMenu>> dupeMenu = registerMenuType("dupe_menu", DupeMenu::new);

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return menuTypeRegister.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus) {
        menuTypeRegister.register(eventBus);
    }
}
