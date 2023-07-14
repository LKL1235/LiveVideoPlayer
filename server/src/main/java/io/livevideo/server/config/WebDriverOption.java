package io.livevideo.server.config;

import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebDriverOption {
    
    @Bean
    public ChromeOptions WebDriverOptions(){
        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--auto-open-devtools-for-tabs");
        return options;
    }
}
