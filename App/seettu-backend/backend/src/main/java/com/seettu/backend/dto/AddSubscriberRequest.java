package com.seettu.backend.dto;

import lombok.Data;

@Data
public class AddSubscriberRequest {
    private String name;
    private String email;
    private String phoneNumber;
    private String password;
    // Removed userId field - will be auto-generated
}