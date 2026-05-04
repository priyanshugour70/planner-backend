package com.planner.dtos.req.sync;

import lombok.Data;

@Data
public class SyncItem {
    private String uuid;
    private String entityType;
    private String action;
    private Long timestamp;
    private Object data;
}
