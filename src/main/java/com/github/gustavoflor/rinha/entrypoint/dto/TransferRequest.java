package com.github.gustavoflor.rinha.entrypoint.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.gustavoflor.rinha.core.TransferType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record TransferRequest(@JsonProperty("valor") @Positive @NotNull Integer value,
                              @JsonProperty("tipo") @NotNull TransferType type,
                              @JsonProperty("descricao") @NotBlank @Size(min = 1, max = 10) String description) {
}
