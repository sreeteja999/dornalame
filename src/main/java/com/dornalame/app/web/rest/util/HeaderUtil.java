package com.dornalame.app.web.rest.util;

import org.springframework.http.HttpHeaders;

/**
 * Utility class for HTTP headers creation.
 *
 */
public class HeaderUtil {

    public static HttpHeaders createAlert(String message, String param) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-mothersDayApp-alert", message);
        headers.add("X-mothersDayApp-params", param);
        return headers;
    }

    public static HttpHeaders createEntityCreationAlert(String entityName, String param) {
        return createAlert("mothersDayApp." + entityName + ".created", param);
    }

    public static HttpHeaders createEntityUpdateAlert(String entityName, String param) {
        return createAlert("mothersDayApp." + entityName + ".updated", param);
    }

    public static HttpHeaders createEntityDeletionAlert(String entityName, String param) {
        return createAlert("mothersDayApp." + entityName + ".deleted", param);
    }

    public static HttpHeaders createFailureAlert(String entityName, String errorKey, String defaultMessage) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-mothersDayApp-error", "error." + errorKey);
        headers.add("X-mothersDayApp-params", entityName);
        return headers;
    }
}
