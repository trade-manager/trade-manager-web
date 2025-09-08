package org.trade.web.rest.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public record LoginRequest(

        @Schema(description = "Unique username") @NotBlank String username,
        @Schema(description = "User password") @NotBlank String password) {
}
