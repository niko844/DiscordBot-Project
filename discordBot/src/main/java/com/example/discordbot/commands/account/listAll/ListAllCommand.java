package com.example.discordbot.commands.account.listAll;

import com.example.discordbot.commands.Command;
import com.example.discordbot.commands.account.entities.Account;
import com.example.discordbot.services.AccountService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.example.discordbot.commands.account.add.AddCommand.formatNumber;

@Component
public class ListAllCommand extends Command {
    @Value("${items-per-page}")
    private int ITEMS_PER_PAGE;
    private final AccountService accountService;

    protected ListAllCommand(AccountService accountService) {
        super("account-listall");
        this.accountService = accountService;
    }

    @Override
    public void handleSlashCommand(SlashCommandInteractionEvent event) {
        if (event.getUser().isBot()) {
            return;
        }

        int page = Optional.ofNullable(event.getOption("page-number"))
                .map(option -> option.getAsInt())
                .orElse(1);

        List<Account> allAccounts = accountService.findAllAccounts();
        sendMessage(event, allAccounts, page);
    }

    private void sendMessage(SlashCommandInteractionEvent event, List<Account> allAccounts, int page) {
        int start = (page - 1) * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, allAccounts.size());

        if (start >= allAccounts.size() || start < 0) {
            event.reply("❌ **Invalid page number**").setEphemeral(true).queue();
            return;
        }

        StringBuilder message = new StringBuilder("📜 **Account List (Page " + page + ")**:\n\n");
        long totalPrice = 0;

        for (int i = start; i < end; i++) {
            Account account = allAccounts.get(i);
            message.append(getFormattedAccountDetails(account));
            totalPrice += account.getPrice();
        }

        message.append("\n💰 **Total Price:** `").append(formatNumber(totalPrice)).append("`$");

        event.reply(message.toString()).queue();
    }

    private String getFormattedAccountDetails(Account account) {
        return String.format("🔹 **Account ID:** %d\n📌 **Category:** %s\n🛒 **Name:** %s\n💵 **Price:** %s$\n👤 **Source:** <@%s>\n\n",
                account.getId(), account.getCategory(), account.getName(), formatNumber(account.getPrice()), account.getSource());
    }
}
