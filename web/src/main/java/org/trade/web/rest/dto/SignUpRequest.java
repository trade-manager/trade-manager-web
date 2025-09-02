package org.trade.web.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public record SignUpRequest(

        @Schema(example = "user3") @NotBlank String username,
        @Schema(example = "user3") @NotBlank String password,
        @Schema(example = "User3") @NotBlank String name,
        @Schema(example = "user3@global.com") @Email String email) {
}
