package ca.example.product.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

    //Timeouts
    public static final String GATEWAY_TIMEOUT = "30000";

    // Channel names
    public static final String OUTBOUND_HTTP_GATEWAY_CHANNEL = "outboundHttpRequestChannel";
    public static final String CREATE_SHIPMENT_CHANNEL = "createShipmentChannel";
    public static final String REVERT_TO_ORIGINAL_PAYLOAD_CHANNEL = "revertToOriginalPayloadChannel";

    // Error Channels
    public static final String GATEWAY_ERROR_CHANNEL = "gatewayErrorChannel";
    public static final String GATEWAY_TIMEOUT_ERROR_CHANNEL = "gatewayTimeoutErrorChannel";
    public static final String COMMON_ERROR_CHANNEL = "commonErrorChannel";

    public static final String PREFIX = "processor_";
    public static final String SUCCEEDING_CHANNEL = PREFIX + "succeeding_channel";
    public static final String ORIGINAL_PAYLOAD = PREFIX + "original_payload";

    public static final String CREATE_SHIPMENT_INTERMEDIARY_CHANNEL = "createShipmentIntermediaryChannel";
    public static final String CREATE_SHIPMENT_HEAD_CHANNEL = "createShipmentHeadChannel";
    public static final String CREATE_SHIPMENT_DETAILS_CHANNEL = "createShipmentDetailsChannel";


}
