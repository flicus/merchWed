package org.schors.merch;

import org.slyrack.telegrambots.core.UpdateHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(BotProperties.class)
public class BotConfig {

    @Bean
    public TelegramBot telegramBot(final BotProperties botProperties, final UpdateHandler updateHandler) {
        return new TelegramBot(botProperties, updateHandler);
    }

    @Bean
    public BotProperties botProperties() {
        return new BotProperties();
    }

}
