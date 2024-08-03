package com.example.discordbot.commands.account.view;

import com.example.discordbot.commands.Command;
import com.example.discordbot.commands.account.add.AddCommand;
import com.example.discordbot.commands.account.entities.Account;
import com.example.discordbot.services.AccountService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Optional;

import static com.example.discordbot.commands.account.add.AddCommand.formatNumber;

@Component
public class ViewCommand extends Command {

    private final AddCommand addCommand;
    private final AccountService accountService;

    protected ViewCommand(AddCommand addCommand, AccountService accountService) {
        super("account-view");
        this.addCommand = addCommand;
        this.accountService = accountService;
    }

    @Override
    public void handleSlashCommand(SlashCommandInteractionEvent event) throws SQLException {
        if (event.getUser().isBot()){
            return;
        }

        String command = event.getName();

        if (!command.equals("account-view")){
            return;
        }

        Long id = event.getOption("id").getAsLong();
        Optional<Account> optionalAccount = accountService.findAccountById(id);
        Account account = optionalAccount.get();

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(0x0099FF);
        embed.setAuthor("Discord bot", botLink, botImage);
        embed.setTitle("__Account Listing (Quantity " + account.getQuantity() + ")__");
        embed.setThumbnail(botImage);
        embed.setDescription("**" + account.getName() + "**");
        embed.addField("🧙‍♂️ __Price & ID__", "🆔 **" + account.getId() + "** - 📈 **$" + account.getPrice() + "** - 💵 **" + formatNumber(account.getPrice()*6666666)+ "**", false);
        embed.setImage(account.getImageUrl());
        embed.setTimestamp(Instant.now());

        event.replyEmbeds(embed.build()).addActionRow(Button.success("buy-button", "🎫 Buy here")).queue();


    }
}
