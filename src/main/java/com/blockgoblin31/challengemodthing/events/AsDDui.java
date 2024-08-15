package com.blockgoblin31.challengemodthing.events;

import com.blockgoblin31.challengemodthing.ChallengeMod;
import com.blockgoblin31.challengemodthing.commands.AsuiUd;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ChallengeMod.MODID)
public class AsDDui {

    @SubscribeEvent
    static void aSDdui(RegisterCommandsEvent asdDUi) {
        AsuiUd.asUiuD(asdDUi.getDispatcher());
    }
}
