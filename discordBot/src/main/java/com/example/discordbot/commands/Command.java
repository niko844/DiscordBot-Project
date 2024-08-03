package com.example.discordbot.commands;

import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.sql.SQLException;


public abstract class Command {

    @Getter
    public final String botImage = "https://media.4-paws.org/b/8/d/5/b8d5441fec6b84e9c3cba899549b84bb0f193fff/VIER%20PFOTEN_2019-07-18_013-2890x2000.jpg";

    @Getter
    public final String botLink = "https://discord.com/oauth2/authorize?client_id=1087060741242228827&permissions=8&integration_type=0&scope=applications.commands+bot";

    private final String name;
    private final String[] aliases;
    private String usage;


    protected Command(String name, String... aliases) {
        this.name = name;
        this.aliases = aliases;
    }


    public abstract void handleSlashCommand(SlashCommandInteractionEvent event) throws SQLException;

    public String getName() {
        return name;
    }

    public String[] getAliases() {
        return aliases;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getUsage() {
        return usage;
    }


}
