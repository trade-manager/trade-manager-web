package org.trade.web.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public record CreateUserRequest(

        @Schema(description = "name") @NotBlank String name,
        @Schema(description = "role") @NotBlank String role) {
}
