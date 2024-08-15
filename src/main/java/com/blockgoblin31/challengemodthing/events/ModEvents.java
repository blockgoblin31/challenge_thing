package com.blockgoblin31.challengemodthing.events;

import com.blockgoblin31.challengemodthing.ChallengeMod;
import com.blockgoblin31.challengemodthing.commands.DenyCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ChallengeMod.MODID)
public class ModEvents {

    @SubscribeEvent
    static void listen(RegisterCommandsEvent asdDUi) {
        DenyCommand.register(asdDUi.getDispatcher());
    }
}
