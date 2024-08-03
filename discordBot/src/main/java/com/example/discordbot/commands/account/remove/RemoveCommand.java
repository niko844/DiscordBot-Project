package com.example.discordbot.commands.account.remove;

import com.example.discordbot.commands.Command;
import com.example.discordbot.commands.account.entities.Account;
import com.example.discordbot.services.AccountService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Optional;

@Component
public class RemoveCommand extends Command {

    private final AccountService accountService;

    @Autowired
    protected RemoveCommand(AccountService accountService) {
        super("account-remove");
        this.accountService = accountService;
    }

    @Override
    public void handleSlashCommand(SlashCommandInteractionEvent event) throws SQLException {
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

        event.reply("Removed Account id **" + id + "** from listing").queue();

    }
}
