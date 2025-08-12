package com.seettu.backend.dto;

import lombok.Data;

@Data
public class CreatePackageRequest {
    private String packageName;
    private String description;
    private Double packageValue;
}