package com.example.discordbot.commands.account.edit;

import com.example.discordbot.commands.Command;
import com.example.discordbot.commands.account.entities.Account;
import com.example.discordbot.services.AccountService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.RestAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Optional;

import static com.example.discordbot.commands.account.add.AddCommand.formatNumber;

@Component
public class EditCommand extends Command {


    private final AccountService accountService;

    @Autowired
    public EditCommand(AccountService accountService) {
        super("account-edit");
        this.accountService = accountService;
    }

    @Override
    public void handleSlashCommand(SlashCommandInteractionEvent event) throws SQLException {
        if (event.getUser().isBot()) {
            return;
        }

        String command = event.getName();

        if (!command.equals("account-edit")) {
            return;
        }

        Long accountId = event.getOption("id").getAsLong();
        Optional<Long> newPrice = event.getOption("price") != null ? Optional.of(event.getOption("price").getAsLong()) : Optional.empty();
        Optional<Integer> newQuantity = event.getOption("quantity") != null ? Optional.of(event.getOption("quantity").getAsInt()) : Optional.empty();

        Optional<Account> optionalAccount = accountService.findAccountById(accountId);

        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            newPrice.ifPresent(account::setPrice);
            newQuantity.ifPresent(account::setQuantity);
            accountService.updateAccount(account);

            if (account.getMessageId() != null) {
                RestAction<Message> messageAction = event.getGuild().getTextChannelById("1263974502665027738").retrieveMessageById(account.getMessageId());
                messageAction.queue(
                        message -> {
                            EmbedBuilder newEmbed = new EmbedBuilder(message.getEmbeds().get(0));
                            newEmbed.getFields().clear();
                            newEmbed.addField("🧙‍♂️ __Price & ID__", "**🆔 " + account.getId() + " - 📈 $" + account.getPrice() + " - 💵 " + formatNumber(account.getPrice() * 6666666) + "**", false);
                            newEmbed.setTitle("__Account Listing (Quantity " + account.getQuantity() + ")__");
                            message.editMessageEmbeds(newEmbed.build()).queue();
                        }
                );
                event.reply("**Updated Account id " + account.getId() + "**").queue();
            } else {
                event.reply("Message ID not found for this account.").setEphemeral(true).queue();
            }
        } else {
            event.reply("Account not found!").setEphemeral(true).queue();
        }
    }
}
