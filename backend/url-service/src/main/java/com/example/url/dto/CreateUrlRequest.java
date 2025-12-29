package com.example.url.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUrlRequest {

    @NotBlank(message = "Original URL is required")
    private String originalUrl;

    // optional
    @Size(min = 3, max = 9, message = "Custom alias must be between 3 and 9 characters")
    @Pattern(
            regexp = "^[a-zA-Z0-9-_]*$",
            message = "Custom alias can contain only letters, numbers, hyphen and underscore"
    )
    private String customAlias;
}
