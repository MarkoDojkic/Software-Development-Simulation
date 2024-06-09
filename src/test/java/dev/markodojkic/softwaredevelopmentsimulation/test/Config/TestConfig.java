package dev.markodojkic.softwaredevelopmentsimulation.test.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;

@Configuration
public class TestConfig {
	@Bean(name = "information.input")
	DirectChannel informationInput(){
		return new DirectChannel();
	}
}