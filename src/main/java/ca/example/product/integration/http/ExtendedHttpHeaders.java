package ca.example.product.integration.http;

import org.springframework.integration.http.HttpHeaders;

public abstract class ExtendedHttpHeaders extends HttpHeaders {
    public static final String REQUEST_PARAMS = PREFIX + "requestParams";
}
