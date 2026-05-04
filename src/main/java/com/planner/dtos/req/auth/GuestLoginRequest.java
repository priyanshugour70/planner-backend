package com.planner.dtos.req.auth;

import lombok.Data;

@Data
public class GuestLoginRequest {
    private String deviceId;
    private String deviceInfo;
}
