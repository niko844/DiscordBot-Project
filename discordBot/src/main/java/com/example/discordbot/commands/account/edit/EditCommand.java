package com.example.discordbot.commands.account.edit;

import com.example.discordbot.commands.Command;
import com.example.discordbot.commands.account.entities.Account;
import com.example.discordbot.services.AccountService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.RestAction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.example.discordbot.commands.account.add.AddCommand.formatNumber;

@Component
public class EditCommand extends Command {

    private final AccountService accountService;
    @Value("${discord.channel-id}")
    public String CHANNEL_ID;

    public EditCommand(AccountService accountService) {
        super("account-edit");
        this.accountService = accountService;
    }

    @Override
    public void handleSlashCommand(SlashCommandInteractionEvent event) {
        if (event.getUser().isBot()) {
            return;
        }

        Optional<Long> accountIdOpt = getOptionAsLong(event, "id");
        if (accountIdOpt.isEmpty()) {
            event.reply("Account ID is required!").setEphemeral(true).queue();
            return;
        }

        Long accountId = accountIdOpt.get();
        Optional<Account> optionalAccount = this.accountService.findAccountById(accountId);

        if (optionalAccount.isEmpty()) {
            event.reply("Account not found!").setEphemeral(true).queue();
            return;
        }

        Account account = optionalAccount.get();
        getOptionAsLong(event, "price").ifPresent(account::setPrice);
        getOptionAsInt(event, "quantity").ifPresent(account::setQuantity);
        this.accountService.updateAccount(account);

        if (account.getMessageId() != null) {
            updateMessage(event, account);
        } else {
            event.reply("Message ID not found for this account.").setEphemeral(true).queue();
        }
    }

    private void updateMessage(SlashCommandInteractionEvent event, Account account) {
        var channel = event.getGuild().getTextChannelById(CHANNEL_ID);
        if (channel == null) {
            event.reply("Channel not found!").setEphemeral(true).queue();
            return;
        }

        RestAction<Message> messageAction = channel.retrieveMessageById(account.getMessageId());
        messageAction.queue(
                message -> {
                    EmbedBuilder newEmbed = new EmbedBuilder(message.getEmbeds().get(0));
                    newEmbed.getFields().clear();
                    newEmbed.setTitle("__Account Listing (Quantity " + account.getQuantity() + ")__");
                    newEmbed.addField("🧙‍♂️ __Price & ID__", formatPriceInfo(account), false);
                    message.editMessageEmbeds(newEmbed.build()).queue();
                    event.reply("**Updated Account id " + account.getId() + "**").queue();
                },
                failure -> event.reply("Failed to update message. It may have been deleted.").setEphemeral(true).queue()
        );
    }

    private String formatPriceInfo(Account account) {
        return String.format("🆔 **%s** - 📈 **$%d** - 💵 **%s**",
                account.getId(),
                account.getPrice(),
                formatNumber(account.getPrice() * 6666666));
    }

    private Optional<Long> getOptionAsLong(SlashCommandInteractionEvent event, String optionName) {
        return Optional.ofNullable(event.getOption(optionName))
                .map(option -> option.getAsLong());
    }

    private Optional<Integer> getOptionAsInt(SlashCommandInteractionEvent event, String optionName) {
        return Optional.ofNullable(event.getOption(optionName))
                .map(option -> option.getAsInt());
    }
}
