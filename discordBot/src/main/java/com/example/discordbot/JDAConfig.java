package com.example.discordbot;

import com.example.discordbot.commands.CommandManager;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class JDAConfig {

    private final CommandManager commandManager;

    @Value("${token}")
    private String token;

    @Bean
    public JDA jda() throws LoginException, InterruptedException, IOException {

        return JDABuilder.createDefault(this.token, getIntents())
                .addEventListeners(commandManager)
                .setStatus(OnlineStatus.ONLINE)
                .enableCache(CacheFlag.ACTIVITY, CacheFlag.ONLINE_STATUS)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL)
                .build()
                .awaitReady();
    }

    private List<GatewayIntent> getIntents() {
        return Arrays.asList(GatewayIntent.values());
    }
}
