package ca.example.product.integration.gateway;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.Message;

import static ca.example.product.util.Constants.*;

@MessagingGateway(errorChannel = GATEWAY_ERROR_CHANNEL, defaultReplyTimeout = GATEWAY_TIMEOUT)
public interface ShipmentGateway {
    @Gateway(requestChannel = CREATE_SHIPMENT_CHANNEL)
    Message<Object> createShipment(Message<Object> input);
}
