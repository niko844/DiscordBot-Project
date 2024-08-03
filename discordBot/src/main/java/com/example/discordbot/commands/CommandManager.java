package com.example.discordbot.commands;

import com.example.discordbot.commands.account.add.AddCommand;
import com.example.discordbot.commands.account.edit.EditCommand;
import com.example.discordbot.commands.account.listAll.ListAllCommand;
import com.example.discordbot.commands.account.remove.RemoveCommand;
import com.example.discordbot.commands.account.sold.SoldCommand;
import com.example.discordbot.commands.account.view.ViewCommand;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CommandManager extends ListenerAdapter {

    private final List<Command> commands;

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String command = event.getName();
        Optional<Command> commandData = this.commands.stream().filter(x -> x.getName().equals(command)).findFirst();
        commandData.ifPresent(value -> {
            try {
                value.handleSlashCommand(event);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        List<SlashCommandData> commands = new ArrayList<>();

        //account-add
        OptionData nameOption = new OptionData(OptionType.STRING, "name","account name", true);
        OptionData priceOption = new OptionData(OptionType.STRING, "price","account price", true);
        OptionData imageOption = new OptionData(OptionType.STRING, "image","account image url", true);
        OptionData categoryOption = new OptionData(OptionType.STRING, "category","account category", true);
        OptionData sourceOption = new OptionData(OptionType.USER, "source","account source", true);
        OptionData quantityOption = new OptionData(OptionType.STRING, "quantity","account quantity", true);

        //account-edit
        OptionData idOption = new OptionData(OptionType.STRING, "id", "account id", true);
        OptionData editPriceOption = new OptionData(OptionType.STRING, "price", "edit account price", false);
        OptionData editQuantityOption = new OptionData(OptionType.STRING, "quantity", "edit account quantity", false);

        //account-listall
        OptionData pageNumberOption = new OptionData(OptionType.STRING, "page-number", "page number", true);

        commands.add(Commands.slash("account-add", "add new account to list")
                .addOptions(nameOption, priceOption, imageOption,categoryOption,sourceOption, quantityOption));

        commands.add(Commands.slash("account-edit","edit account quantity or price").addOptions(idOption, editPriceOption,editQuantityOption));

        commands.add(Commands.slash("account-view","find account by id").addOptions(idOption));

        commands.add(Commands.slash("account-listall", "list all accounts").addOptions(pageNumberOption));

        commands.add(Commands.slash("account-remove", "remove account from listing").addOptions(idOption));

        commands.add(Commands.slash("account-sold", "mark account as sold").addOptions(idOption));

        event.getGuild().updateCommands().addCommands(commands).queue();
    }
}

