package com.example.discordbot.commands.account.remove;

import com.example.discordbot.commands.Command;
import com.example.discordbot.commands.account.entities.Account;
import com.example.discordbot.services.AccountService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RemoveCommand extends Command {

    @Value("${discord.channel-id}")
    public String CHANNEL_ID;
    private final AccountService accountService;

    protected RemoveCommand(AccountService accountService) {
        super("account-remove");
        this.accountService = accountService;
    }

    @Override
    public void handleSlashCommand(SlashCommandInteractionEvent event) {

        if (event.getOption("id") == null) {
            event.reply("❌ **Invalid command usage!** Please provide a valid account ID").setEphemeral(true).queue();
            return;
        }

        long id = event.getOption("id").getAsLong();
        Optional<Account> accountById = this.accountService.findAccountById(id);

        if (accountById.isEmpty()) {
            event.reply("⚠️ **Account with ID " + id + " doesn't exist**").setEphemeral(true).queue();
            return;
        }

        Account account = accountById.get();

        if (account.getMessageId() != null) {
            event.getGuild().getTextChannelById(CHANNEL_ID)
                    .deleteMessageById(account.getMessageId())
                    .queue(
                            success -> this.accountService.removeAccountById(id),
                            failure -> event.reply("❌ **Failed to delete the message.** Account removed from the database").setEphemeral(true).queue()
                    );
        } else {
            this.accountService.removeAccountById(id);
        }

        event.reply("✅ **Successfully removed account with ID " + id + "** from the listing**").queue();
    }
}
