package com.example.discordbot.commands;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CommandManager extends ListenerAdapter {

    private final List<Command> commands;

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        commands.stream()
                .filter(command -> command.getName().equals(event.getName()))
                .findFirst()
                .ifPresent(command -> {
                    try {
                        command.handleSlashCommand(event);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        List<SlashCommandData> commands = List.of(
                createSlashCommand("account-add", "add new account to list",
                        option(OptionType.STRING, "name", "account name", true),
                        option(OptionType.STRING, "price", "account price", true),
                        option(OptionType.STRING, "image", "account image url", true),
                        option(OptionType.STRING, "category", "account category", true),
                        option(OptionType.USER, "source", "account source", true),
                        option(OptionType.STRING, "quantity", "account quantity", true)
                ),
                createSlashCommand("account-edit", "edit account quantity or price",
                        option(OptionType.STRING, "id", "account id", true),
                        option(OptionType.STRING, "price", "edit account price", false),
                        option(OptionType.STRING, "quantity", "edit account quantity", false)
                ),
                createSlashCommand("account-view", "find account by id",
                        option(OptionType.STRING, "id", "account id", true)
                ),
                createSlashCommand("account-listall", "list all accounts",
                        option(OptionType.STRING, "page-number", "page number", true)
                ),
                createSlashCommand("account-remove", "remove account from listing",
                        option(OptionType.STRING, "id", "account id", true)
                ),
                createSlashCommand("account-sold", "mark account as sold",
                        option(OptionType.STRING, "id", "account id", true)
                )
        );

        event.getGuild().updateCommands().addCommands(commands).queue();
    }

    private SlashCommandData createSlashCommand(String name, String description, OptionData... options) {
        return Commands.slash(name, description).addOptions(options);
    }

    private OptionData option(OptionType type, String name, String description, boolean required) {
        return new OptionData(type, name, description, required);
    }
}
