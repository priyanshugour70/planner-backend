package com.planner.dtos.res.sync;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncResponse {
    private Long serverTimestamp;
    private Integer version;
    private Map<String, List<Object>> data;
    private List<SyncConflict> conflicts;
}
