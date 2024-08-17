package com.blockgoblin31.challengemodthing.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.ByteTag;
import net.minecraft.server.level.ServerPlayer;

public class DenyCommand {
    public DenyCommand() {

    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("deny").executes((command) -> run(command.getSource())));
    }

    private static int run(CommandSourceStack stack) {
        ServerPlayer player = stack.getPlayer();
        player.getPersistentData().put("bg31.deny", ByteTag.valueOf(true));
        return 1;
    }
}
