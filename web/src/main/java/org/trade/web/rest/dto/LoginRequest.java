package org.trade.web.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public record LoginRequest(

        @Schema(example = "user") @NotBlank String username,
        @Schema(example = "user") @NotBlank String password) {
}
