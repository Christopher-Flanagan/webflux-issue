package ca.example.product.servicebus.function;

import ca.example.product.integration.gateway.ShipmentGateway;
import ca.example.product.integration.gateway.TimeoutGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Objects;
import java.util.function.Function;


@Configuration
@Slf4j
@RequiredArgsConstructor
public class ReturnToVendor {

    private final ShipmentGateway shipmentGateway;
    private final TimeoutGateway timeoutGateway;

    @Bean
    public Function<Message<Object>, Message<Object>> archiveTagStep() {
        return message -> {

            var result = shipmentGateway.createShipment(message);

            if(Objects.isNull(result)) {
                log.debug("Gateway time out reached during the triggering of a RTV create shipment/shipment details, triggering timeout handler.");
                return MessageBuilder.withPayload(new Object()).build();
            }

            return result;
        };
    }
}
