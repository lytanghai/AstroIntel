package com.tanghai.announcement.component;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class RebootComponent  {

    private final AppProperties appProperties;
    private final TelegramSender telegramSender;

    public RebootComponent(AppProperties appProperties, TelegramSender telegramSender) {
        this.appProperties = appProperties;
        this.telegramSender = telegramSender;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        StringBuilder message = new StringBuilder();

        message.append("***** 𝘼𝙎𝙏𝙍𝙊 𝙄𝙉𝙏𝙀𝙇 𝘼𝙋𝙋 𝙎𝙏𝘼𝙍𝙏𝙀𝘿 *****\n");
        message.append("𝙑𝙀𝙍𝙎𝙄𝙊𝙉   : ").append(appProperties.getVersion()).append("\n");
        message.append("What's New?:\n\n");

        if (appProperties.getFeatures() != null && !appProperties.getFeatures().isEmpty()) {
            for (String feature : appProperties.getFeatures()) {
                message.append("   • ").append(feature).append("\n");
            }
        } else {
            return;
        }
        message.append("\n\n********************************************");

        telegramSender.send("678134373", message.toString());
    }
}
