package org.trade.web.rest.response;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public record ErrorResponse(int status, String message, long timestamp) {
}
