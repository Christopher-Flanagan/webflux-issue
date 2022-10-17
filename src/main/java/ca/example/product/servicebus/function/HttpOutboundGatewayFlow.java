package ca.example.product.servicebus.function;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.support.MutableMessageHeaders;
import org.springframework.integration.webflux.dsl.WebFlux;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ca.example.product.integration.http.ExtendedHttpHeaders.REQUEST_PARAMS;
import static ca.example.product.util.Constants.OUTBOUND_HTTP_GATEWAY_CHANNEL;
import static org.springframework.integration.http.HttpHeaders.REQUEST_METHOD;
import static org.springframework.integration.http.HttpHeaders.REQUEST_URL;

@Configuration
@Slf4j
public class HttpOutboundGatewayFlow {
    @Bean
    public IntegrationFlow httpOutboundGateway(WebClient defaultWebClient) {
        return IntegrationFlows.from(OUTBOUND_HTTP_GATEWAY_CHANNEL)
                .log(LoggingHandler.Level.INFO, m ->
                        String.format("Sending request to the following url %s", m.getHeaders().get(REQUEST_URL)))
                .handle(WebFlux.outboundGateway(m -> m.getHeaders().get(REQUEST_URL), defaultWebClient)
                        .uriVariablesFunction(m -> (Map<String, ?>) m.getHeaders()
                                .getOrDefault(REQUEST_PARAMS, new HashMap<>()))
                        .httpMethodFunction(m -> m.getHeaders().get(REQUEST_METHOD))
                        .expectedResponseType(new ParameterizedTypeReference<List<?>>() {}),
                        e -> e.id("httpOutboundGatewayPoint")
                )
                .log(LoggingHandler.Level.DEBUG, m -> String.format("Outbound gateway payload : %s", m.getPayload()))
                .log(LoggingHandler.Level.INFO, m -> String.format("Successful received response from following url %s",
                                m.getHeaders().get(REQUEST_URL)))
                .log(LoggingHandler.Level.INFO, m -> "Retrieving succeeding route.")
                .transform(Message.class, this::headerRemover)
                .route(Message.class, "SOME CHANNEL name")
                .get();
    }

    private Message<?> headerRemover(Message<?> m) {
        var headers = new MutableMessageHeaders(m.getHeaders());
        headers.remove(REQUEST_URL);
        headers.remove(REQUEST_METHOD);
        headers.remove(REQUEST_PARAMS);

        return MessageBuilder.createMessage(m.getPayload(), headers);
    }
}
