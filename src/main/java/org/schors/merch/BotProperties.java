package org.schors.merch;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@ConfigurationProperties("merch")
public class BotProperties {
    private String username;
    private String token;
    private boolean debug;
}
