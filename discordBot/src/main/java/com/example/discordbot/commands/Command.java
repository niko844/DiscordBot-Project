package com.example.discordbot.commands;


import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Value;

import java.sql.SQLException;

@Getter
@Setter
public abstract class Command {

    @Value("${bot.image}")
    public String botImage;

    @Value("${bot.link}")
    public String botLink;

    private final String name;
    private final String[] aliases;
    private String usage;


    protected Command(String name, String... aliases) {
        this.name = name;
        this.aliases = aliases;
    }

    public abstract void handleSlashCommand(SlashCommandInteractionEvent event) throws SQLException;

}
