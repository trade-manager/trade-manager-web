package org.trade.web.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
public record CreateEmployeeRequest(

        @Schema(example = "1234567812") @NotBlank Long id,
        @Schema(example = "James Dean") @NotBlank String name,
        @Schema(example = "James") @NotBlank String firstName,
        @Schema(example = "Dean") @NotBlank String lastName,
        @Schema(example = "James Dean user") @NotBlank String description,
        @Schema(example = "james.dean@global.com") @NotBlank String email) {
}
