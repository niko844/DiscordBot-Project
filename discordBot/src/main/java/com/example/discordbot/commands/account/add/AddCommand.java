package com.example.discordbot.commands.account.add;

import com.example.discordbot.commands.Command;
import com.example.discordbot.commands.account.entities.Account;
import com.example.discordbot.services.AccountService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.springframework.stereotype.Component;


import java.time.Instant;

@Component
public class AddCommand extends Command {

    private final AccountService accountService;

    public AddCommand(AccountService accountService) {
        super("account-add");
        this.accountService = accountService;
    }

    @Override
    public void handleSlashCommand(SlashCommandInteractionEvent event) {

        Account account = new Account();
        account.setName(event.getOption("name").getAsString());
        account.setPrice(event.getOption("price").getAsLong());
        account.setImageUrl(event.getOption("image").getAsString());
        account.setCategory(event.getOption("category").getAsString());
        account.setSource(event.getOption("source").getAsMember().getId());
        account.setQuantity(event.getOption("quantity").getAsInt());

        this.accountService.saveAccount(account);

        sendAccountEmbed(event, account);

    }

    private void sendAccountEmbed(SlashCommandInteractionEvent event, Account account) {
        EmbedBuilder embed = new EmbedBuilder()
                .setColor(0x0099FF)
                .setAuthor("Discord bot", botLink, botImage)
                .setTitle("__Account Listing (Quantity " + account.getQuantity() + ")__")
                .setThumbnail(botImage)
                .setDescription("**" + account.getName() + "**")
                .addField("🧙‍♂️ __Price & ID__", formatPriceInfo(account), false)
                .setImage(account.getImageUrl())
                .setTimestamp(Instant.now());

        event.reply("**Posted Account id " + account.getId() + "**").queue();

        event.getGuild().getTextChannelById("1263974502665027738")
                .sendMessageEmbeds(embed.build())
                .setActionRow(Button.success("buy-button", "🎫 Buy here"))
                .queue(message -> {
                    account.setMessageId(message.getId());
                    this.accountService.updateAccount(account);
                });
    }

    public String formatPriceInfo(Account account) {
        return String.format("🆔 **%s** - 📈 **$%d** - 💵 **%s**",
                account.getId(),
                account.getPrice(),
                formatNumber(account.getPrice() * 5555500));
    }

    public static String formatNumber(long number) {
        if (number >= 1_000_000) {
            return String.format("%.1fM", number / 1_000_000.0);
        } else if (number >= 1_000) {
            return String.format("%.1fK", number / 1_000.0);
        } else {
            return String.valueOf(number);
        }
    }
}
