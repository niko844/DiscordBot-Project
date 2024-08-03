package com.example.discordbot.commands.account.listAll;

import com.example.discordbot.commands.Command;
import com.example.discordbot.commands.account.entities.Account;
import com.example.discordbot.services.AccountService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ListAllCommand extends Command {
    private static final int ITEMS_PER_PAGE = 10;
    private final AccountService accountService;

    @Autowired
    protected ListAllCommand(AccountService accountService) {
        super("account-listall");
        this.accountService = accountService;
    }

    @Override
    public void handleSlashCommand(SlashCommandInteractionEvent event) throws SQLException {
        if (event.getUser().isBot()){
            return;
        }

        String command = event.getName();

        if (!command.equals("account-listall")){
            return;
        }

        int page = event.getOption("page-number").getAsInt();

        List<Account> allAccounts = accountService.findAllAccounts();

        sendMessage(event, allAccounts, page);


    }

    private void sendMessage(SlashCommandInteractionEvent event, List<Account> allAccounts, int page) {
        StringBuilder message = new StringBuilder();
        int start = (page -1) * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, allAccounts.size());

        if (start >= allAccounts.size() || start <0){
            event.reply("Invalid page number.").queue();
            return;
        }
        message.append("List:\n");

        int totalPrice = 0;

        for (int i = start; i < end; i++) {
            Account account = allAccounts.get(i);
            message.append("**Account id ").append(account.getId()).append("**\n")
                    .append("Category: **").append(account.getCategory()).append("** - ")
                    .append("Name: **").append(account.getName()).append("** - ")
                    .append("Price: **").append(account.getPrice()).append("$** - ")
                    .append("Source: <@").append(account.getSource()).append(">\n\n");

            totalPrice += account.getPrice();
        }

        message.append("\nTotal: ").append("`").append(totalPrice).append("`$");

        event.reply(message.toString()).queue();
    }
}
