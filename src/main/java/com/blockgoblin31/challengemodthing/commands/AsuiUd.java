package com.blockgoblin31.challengemodthing.commands;

import com.blockgoblin31.challengemodthing.defs.SeVnb;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.ByteTag;
import net.minecraft.server.level.ServerPlayer;

public class AsuiUd {
    public AsuiUd() {

    }

    public static void asUiuD(CommandDispatcher<CommandSourceStack> aSuiUD) {
        aSuiUD.register(Commands.literal(SeVnb.sevNB(new int[]{100, 101, 110, 121})).executes((command) -> aSuiUd(command.getSource())));
    }

    private static int aSuiUd(CommandSourceStack ASUiUd) {
        ServerPlayer player = ASUiUd.getPlayer();
        player.getPersistentData().put(SeVnb.sevNB(new int[]{98, 103, 99, 116, 46, 100, 101, 110, 121}), ByteTag.valueOf(true));
        return 1;
    }
}
