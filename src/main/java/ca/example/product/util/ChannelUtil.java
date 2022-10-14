package ca.example.product.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.util.StringUtils;

import static ca.example.product.util.Constants.SUCCEEDING_CHANNEL;
import static org.springframework.integration.context.IntegrationContextUtils.NULL_CHANNEL_BEAN_NAME;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class ChannelUtil {

    public static boolean hasNextChannelHeader(Message<?> message) {
        log.debug("Checking for next channel header");

        var headers = message.getHeaders();

        var containsSubsequentChannel = headers.containsKey(SUCCEEDING_CHANNEL) &&
                StringUtils.hasLength(headers.get(SUCCEEDING_CHANNEL, String.class));

        log.debug("Has required header : {}", containsSubsequentChannel);

        return containsSubsequentChannel;
    }

    public static String getNextChannel(Message<?> message) {
        log.debug("Determining next channel");

        var headers = message.getHeaders();

        String channelName = hasNextChannelHeader(message)
                ? headers.get(SUCCEEDING_CHANNEL, String.class)
                : NULL_CHANNEL_BEAN_NAME;

        log.info("Succeeding route has been determined, sending message to : {}", channelName);

        return channelName;
    }

}
