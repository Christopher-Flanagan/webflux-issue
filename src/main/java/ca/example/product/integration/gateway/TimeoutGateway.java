package ca.example.product.integration.gateway;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.Message;

import static ca.example.product.util.Constants.GATEWAY_TIMEOUT;
import static ca.example.product.util.Constants.GATEWAY_TIMEOUT_ERROR_CHANNEL;


@MessagingGateway(defaultReplyTimeout = GATEWAY_TIMEOUT)
public interface TimeoutGateway {
    @Gateway(requestChannel = GATEWAY_TIMEOUT_ERROR_CHANNEL)
    Message<Object> handleGatewayTimeout(Message<Object> input);
}
