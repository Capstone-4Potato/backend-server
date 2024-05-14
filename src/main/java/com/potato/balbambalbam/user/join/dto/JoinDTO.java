package com.potato.balbambalbam.user.join.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class JoinDTO {
    @NotBlank
    private String name;
    @NotBlank
    private String socialId;
    @NotBlank
    private Integer age;
    @NotBlank
    private Byte gender;

}
