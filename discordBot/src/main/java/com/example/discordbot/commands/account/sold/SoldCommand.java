package com.example.discordbot.commands.account.sold;

import com.example.discordbot.commands.Command;
import com.example.discordbot.commands.account.entities.Account;
import com.example.discordbot.services.AccountService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Optional;

@Component
public class SoldCommand extends Command {
    private final AccountService accountService;

    @Autowired
    protected SoldCommand(AccountService accountService) {
        super("account-sold");
        this.accountService = accountService;
    }

    @Override
    public void handleSlashCommand(SlashCommandInteractionEvent event) {

        if (event.getUser().isBot()){
            return;
        }

        Long id = event.getOption("id").getAsLong();
        Optional<Account> accountById = accountService.findAccountById(id);
        if (accountById.isEmpty()){
            throw new RuntimeException("Account doesn't exist");
        }
        Account account = accountById.get();

        String messageId = account.getMessageId();
        event.getGuild().getTextChannelById("1263974502665027738").deleteMessageById(messageId).queue();
        accountService.removeAccountById(id);

        event.reply("Account with id **" + id + "** marked as sold").queue();
    }
}
