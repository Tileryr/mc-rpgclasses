package com.mcclasses.rpgclassabilities.commands;

import com.mcclasses.rpgclassabilities.Rpgclassabilities;
import com.mcclasses.rpgclassabilities.enums.RpgClass;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.concurrent.CompletableFuture;

public class SetClassCommand {

    public static class RpgClassSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
        @Override
        public CompletableFuture<Suggestions> getSuggestions(final CommandContext<ServerCommandSource> context, final SuggestionsBuilder builder) throws CommandSyntaxException {
            for (RpgClass rpgClass : RpgClass.values()) {
                builder.suggest(rpgClass.asString());
            }
            return builder.buildFuture();
        }
    }

    public static int setClass(String newClass, CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        for (RpgClass rpgClass : RpgClass.values()) {
            if (!newClass.equals(rpgClass.asString())) {
                continue;
            }
            ServerPlayerEntity player = context.getSource().getPlayer();
            Rpgclassabilities.setRpgClass(rpgClass, player);
            String playerName = player.getGameProfile().getName();
            context.getSource().sendFeedback(() -> Text.literal("Set " + playerName + "'s class to: " + rpgClass.asString()), true);
            return Command.SINGLE_SUCCESS;
        }
        LiteralMessage message = new LiteralMessage("Invalid class type!");
        throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
    }
}
