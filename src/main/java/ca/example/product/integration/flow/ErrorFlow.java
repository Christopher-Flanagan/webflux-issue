package ca.example.product.integration.flow;

import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;

import static ca.example.product.util.Constants.*;
import static org.springframework.integration.handler.LoggingHandler.Level.ERROR;
import static org.springframework.integration.handler.LoggingHandler.Level.INFO;

public class ErrorFlow {

    @Bean
    public IntegrationFlow gatewayErrorFlow() {
        return IntegrationFlows.from(GATEWAY_ERROR_CHANNEL)
                .log(ERROR, m -> "An error has occurred! please verify the payload is correct and the external applications are accessible")
                .<Exception>log(ERROR, m -> "error reason : " + m.getPayload().getCause().getMessage())
                .log(INFO, m -> "Gateway error channel has been triggered")
                .channel(COMMON_ERROR_CHANNEL)
                .get();
    }

    @Bean
    public IntegrationFlow gatewayTimeoutErrorFlow() {
        return IntegrationFlows.from(GATEWAY_TIMEOUT_ERROR_CHANNEL)
                .log(ERROR, m -> "An error has occurred! please verify the payload is correct and the external applications are accessible")
                .log(INFO, m -> "Gateway timeout error channel has been triggered")
                .channel(COMMON_ERROR_CHANNEL)
                .get();
    }

    @Bean
    public IntegrationFlow commonErrorFlow() {
        return IntegrationFlows.from(COMMON_ERROR_CHANNEL)
                //removed error handling transformers/handlers
                .logAndReply();
    }
}
