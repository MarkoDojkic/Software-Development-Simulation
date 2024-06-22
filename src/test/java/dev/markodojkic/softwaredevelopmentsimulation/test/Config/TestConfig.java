package dev.markodojkic.softwaredevelopmentsimulation.test.Config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;

import java.nio.charset.StandardCharsets;

@TestConfiguration
public class TestConfig {
    @Bean
    @Primary
    public MqttPahoClientFactory testMqttPahoClientFactory() {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setServerURIs(new String[]{"tcp://0.0.0.0:21681"});
        mqttConnectOptions.setUserName("");
        mqttConnectOptions.setPassword("".toCharArray());
        mqttConnectOptions.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setWill("information-printout-topic", "Testing session ended".getBytes(StandardCharsets.UTF_8), 1, false);
        mqttConnectOptions.setKeepAliveInterval(3600);
        DefaultMqttPahoClientFactory defaultMqttPahoClientFactory = new DefaultMqttPahoClientFactory();
        defaultMqttPahoClientFactory.setConnectionOptions(mqttConnectOptions);
        return defaultMqttPahoClientFactory;
    }

    @Bean
    @Primary
    public MqttPahoMessageHandler testMqttPahoMessageHandler() {
        MqttPahoMessageHandler mqttPahoMessageHandler = new MqttPahoMessageHandler("be-client-test", testMqttPahoClientFactory());
        mqttPahoMessageHandler.setDefaultTopic("default");
        mqttPahoMessageHandler.setAsync(true);
        mqttPahoMessageHandler.setDefaultRetained(false);
        mqttPahoMessageHandler.setDefaultQos(1);
        mqttPahoMessageHandler.setConverter(new DefaultPahoMessageConverter());
        return mqttPahoMessageHandler;
    }
}