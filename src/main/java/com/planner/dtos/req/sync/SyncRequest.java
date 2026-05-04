package com.planner.dtos.req.sync;

import lombok.Data;

import java.util.List;

@Data
public class SyncRequest {
    private Integer version = 3;
    private Long lastSyncTimestamp;
    private List<SyncItem> items;
}
