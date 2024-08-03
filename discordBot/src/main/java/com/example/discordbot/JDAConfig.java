package com.example.discordbot;

import com.example.discordbot.commands.CommandManager;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.security.auth.login.LoginException;
import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class JDAConfig {

    private final CommandManager commandManager;

   @Bean
    public JDA jda() throws LoginException, InterruptedException {
        Dotenv config = Dotenv.configure().directory("D:\\IntelliJ IDEA Community Edition 2022.2.2\\Bot2\\Bot2\\.env").load();
        String token = config.get("TOKEN");

       return JDABuilder.createDefault(token, getIntents())
               .addEventListeners(commandManager) // Register CommandManager as a Spring bean
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
