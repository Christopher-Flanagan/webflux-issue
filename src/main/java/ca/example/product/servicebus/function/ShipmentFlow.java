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
                    h.header(REQUEST_URL, "http://localhost:64885/");
                    h.header(REQUEST_METHOD, HttpMethod.HEAD);
                    //h.header(REQUEST_PARAMS,uriVariableExpressions);
                })
                .channel(OUTBOUND_HTTP_GATEWAY_CHANNEL)
                .get();
    }

    @Bean
    public IntegrationFlow createShipmentIntermediaryFlow() {
        return IntegrationFlows.from(CREATE_SHIPMENT_INTERMEDIARY_CHANNEL)
                .log(INFO, m -> "Entering create shipment intermediate section")
                .route(Message.class, m -> {
                            var httpStatus = (HttpStatus) m.getHeaders().get(STATUS_CODE);
                            return NO_CONTENT.equals(httpStatus);
                        }, f -> f
                                .subFlowMapping(true, f1 -> f1
                                        .log(INFO, m -> "No shipment record has been found.")
                                        .channel(CREATE_SHIPMENT_HEAD_CHANNEL))
                                .subFlowMapping(false, f2 -> f2
                                        .log(INFO, m -> "A shipment record has been found.")
                                        .channel(CREATE_SHIPMENT_DETAILS_CHANNEL))
                )
                .get();
    }

    @Bean
    public IntegrationFlow createShipmentHeadFlow() {
        return IntegrationFlows.from(CREATE_SHIPMENT_HEAD_CHANNEL)
                .log(INFO, m -> "Entering create shipment head section")
                .transform(Message.class, m -> {
                    var originalPayload = m.getHeaders().get(ORIGINAL_PAYLOAD);
                    return MessageBuilder.createMessage(originalPayload, m.getHeaders());
                })
                //.transform(Message.class, transformer::transform)
                .enrichHeaders(h -> {
                    h.header(REQUEST_URL, "");
                    h.header(REQUEST_METHOD, HttpMethod.POST);
                    h.header(SUCCEEDING_CHANNEL, REVERT_TO_ORIGINAL_PAYLOAD_CHANNEL, true);
                })
                .channel(OUTBOUND_HTTP_GATEWAY_CHANNEL)
                .get();
    }

    @Bean
    public IntegrationFlow createShipmentDetailFlow() {
        return IntegrationFlows.from(CREATE_SHIPMENT_DETAILS_CHANNEL)
                .log(INFO, m -> "Entering create shipment details section")
                .transform(Message.class, m -> {
                    var originalPayload = m.getHeaders().get(ORIGINAL_PAYLOAD);
                    return MessageBuilder.createMessage(originalPayload, m.getHeaders());
                })
                .enrichHeaders(h -> {
                    var uriVariableExpressions = new HashMap<String, Expression>();
                    uriVariableExpressions.put("ID", PARSER.parseExpression("headers['" + ORIGINAL_PAYLOAD + "'].shipment"));

                    h.header(REQUEST_URL, "");
                    h.header(REQUEST_METHOD, HttpMethod.POST);
                    h.header(REQUEST_PARAMS, uriVariableExpressions);
                    h.header(SUCCEEDING_CHANNEL, REVERT_TO_ORIGINAL_PAYLOAD_CHANNEL, true);
                })
                .channel(OUTBOUND_HTTP_GATEWAY_CHANNEL)
                .get();
    }
}
