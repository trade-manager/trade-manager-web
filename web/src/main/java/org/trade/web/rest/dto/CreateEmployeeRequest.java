package org.trade.web.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public record CreateEmployeeRequest(

        @Schema(description = "name") @NotBlank String name,
        @Schema(description = "firstName") @NotBlank String firstName,
        @Schema(description = "lastName") @NotBlank String lastName,
        @Schema(description = "email") @NotBlank String email,
        @Schema(description = "user") CreateUserRequest user) {
}
