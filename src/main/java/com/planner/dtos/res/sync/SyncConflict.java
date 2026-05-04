package com.planner.dtos.res.sync;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncConflict {
    private String uuid;
    private String entityType;
    private String resolution;
    private Object serverVersion;
    private Object clientVersion;
}
