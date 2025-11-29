package org.trade.web.rest.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public record AuthResponse(
        @Schema(description = "Unique id") @NotBlank Long id,
        @Schema(description = "Unique user name") @NotBlank String name,
        @Schema(description = "Unique role name") @NotBlank String role) {
}
