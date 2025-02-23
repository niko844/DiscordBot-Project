package com.example.discordbot.commands.account.sold;

import com.example.discordbot.commands.Command;
import com.example.discordbot.commands.account.entities.Account;
import com.example.discordbot.services.AccountService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SoldCommand extends Command {
    private final AccountService accountService;


    protected SoldCommand(AccountService accountService) {
        super("account-sold");
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
            event.getGuild().getTextChannelById("1263974502665027738")
                    .deleteMessageById(account.getMessageId())
                    .queue(
                            success -> {
                                this.accountService.removeAccountById(id);
                                event.reply("✅ **Account with ID " + id + " marked as sold and removed**").queue();
                            },
                            failure -> event.reply("⚠️ **Failed to delete the message.** Account removed from the database").setEphemeral(true).queue()
                    );
        } else {
            this.accountService.removeAccountById(id);
            event.reply("✅ **Account with ID " + id + " marked as sold**").queue();
        }
    }
}
