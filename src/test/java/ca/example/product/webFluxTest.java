package ca.example.product;

import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.test.context.SpringIntegrationTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static com.azure.spring.messaging.servicebus.support.ServiceBusMessageHeaders.RECEIVED_MESSAGE_CONTEXT;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {"spring.cloud.stream.function.definition=createShipmentStep"})
@ExtendWith(OutputCaptureExtension.class)
@ActiveProfiles("local")
@SpringIntegrationTest
@Import({TestChannelBinderConfiguration.class})
class webFluxTest {

    @Autowired
    private InputDestination source;
    @Autowired
    private OutputDestination target;

    protected Message<?> inputMessage;

    public static MockWebServer mockBackEnd;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start(64885);
    }


    @Test
    void createShipmentTest(CapturedOutput output) throws InterruptedException {
        for(int i = 0; i < 1; i++) {
            inputMessage = MessageBuilder.withPayload(i + "A")
                    .build();

            var inputTwo = MessageBuilder.withPayload(i + "B")
                    .build();


            mockBackEnd.enqueue(new MockResponse()
                    .setResponseCode(204)
                    .addHeader("Content-Type", "application/json"));

            source.send(inputMessage, "ds_shipment_topic_dev");
            source.send(inputTwo, "ds_shipment_topic_dev");
            var result = target.receive(5000, "ds_transfer_out_topic_dev");
            assertNotNull(result);

            result = target.receive(10000, "ds_transfer_out_topic_dev");
            assertNotNull(result);

            assertFalse(output.getOut().contains("Gateway time out reached during the triggering of a RTV create shipment/shipment details, triggering timeout handler."));
        }
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }
}
