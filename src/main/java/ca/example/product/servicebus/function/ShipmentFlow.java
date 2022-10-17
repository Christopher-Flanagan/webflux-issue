package ca.example.product.servicebus.function;

import org.springframework.context.annotation.Bean;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.HashMap;

import static ca.example.product.integration.http.ExtendedHttpHeaders.REQUEST_PARAMS;
import static ca.example.product.util.Constants.*;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.integration.handler.LoggingHandler.Level.INFO;
import static org.springframework.integration.http.HttpHeaders.*;

@Component
public class ShipmentFlow {

    private static final SpelExpressionParser PARSER = new SpelExpressionParser();

    private static final String PAYLOAD_SHIPMENT_EXPRESSION_STRING = "payload.shipment";

    @Bean
    public IntegrationFlow createShipmentFlow() {
        return IntegrationFlows.from(CREATE_SHIPMENT_CHANNEL)
                .log(INFO, m -> "Entering the create shipment section")
                .enrichHeaders(h -> {
                    var uriVariableExpressions = new HashMap<String, Expression>();
                    uriVariableExpressions.put("ID", PARSER.parseExpression(PAYLOAD_SHIPMENT_EXPRESSION_STRING));

                    h.headerExpression(ORIGINAL_PAYLOAD, "payload");
                    h.header(SUCCEEDING_CHANNEL, CREATE_SHIPMENT_INTERMEDIARY_CHANNEL);
                    h.header(REQUEST_URL, "");
                    h.header(REQUEST_METHOD, HttpMethod.HEAD);
                    h.header(REQUEST_PARAMS,uriVariableExpressions);
                })
                .channel(OUTBOUND_HTTP_GATEWAY_CHANNEL)
                .get();
    }
}
