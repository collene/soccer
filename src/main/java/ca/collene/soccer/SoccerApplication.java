package ca.collene.soccer;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.shell.jline.PromptProvider;

@SpringBootApplication
@ComponentScan(basePackages = "ca.collene")
@EntityScan("ca.collene")
@EnableJpaRepositories("ca.collene")
public class SoccerApplication {
	public static void main(String[] args) {
		SpringApplication.run(SoccerApplication.class, args);						
	}

	@Bean
    public PromptProvider myPromptProvider() {
        return () -> new AttributedString("soccer:>",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
    }
}
