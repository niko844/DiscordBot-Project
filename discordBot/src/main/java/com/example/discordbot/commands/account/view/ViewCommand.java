package com.example.discordbot.commands.account.view;

import com.example.discordbot.commands.Command;
import com.example.discordbot.commands.account.entities.Account;
import com.example.discordbot.services.AccountService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.stereotype.Component;

import java.time.Instant;

import static com.example.discordbot.commands.account.add.AddCommand.formatNumber;

@Component
public class ViewCommand extends Command {

    private final AccountService accountService;

    protected ViewCommand(AccountService accountService) {
        super("account-view");
        this.accountService = accountService;
    }

    @Override
    public void handleSlashCommand(SlashCommandInteractionEvent event){
        if (event.getUser().isBot()) return;

        long id = event.getOption("id").getAsLong();

        Account account = this.accountService.findAccountById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        EmbedBuilder embed = new EmbedBuilder()
                .setColor(0x0099FF)
                .setAuthor("Discord bot", botLink, botImage)
                .setTitle("__Account Listing (Quantity " + account.getQuantity() + ")__")
                .setThumbnail(botImage)
                .setDescription("**" + account.getName() + "**")
                .addField("🧙‍♂️ __Price & ID__", formatPriceInfo(account), false)
                .setImage(account.getImageUrl())
                .setTimestamp(Instant.now());

        event.replyEmbeds(embed.build())
                .addActionRow(Button.success("buy-button", "🎫 Buy here"))
                .queue();
    }

    private String formatPriceInfo(Account account) {
        return String.format("🆔 **%d** - 📈 **$%d** - 💵 **%s**",
                account.getId(),
                account.getPrice(),
                formatNumber(account.getPrice() * 6666666));
    }
}
