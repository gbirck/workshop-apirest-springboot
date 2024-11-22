package com.park.demo_park_api.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpotCreateDTO {

    @NotBlank
    @Size(min = 4, max = 4)
    private String code;

    @NotBlank
    @Pattern(regexp = "FREE|OCUPIED")
    private String status;
}
