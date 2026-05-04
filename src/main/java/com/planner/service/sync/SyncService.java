package com.planner.service.sync;

import com.planner.dtos.ServiceResult;
import com.planner.dtos.req.sync.SyncRequest;
import com.planner.dtos.res.sync.FullSyncResponse;
import com.planner.dtos.res.sync.SyncResponse;

public interface SyncService {

    ServiceResult<SyncResponse> pushChanges(SyncRequest request);

    ServiceResult<FullSyncResponse> pullAllData();

    ServiceResult<Void> fullSync(FullSyncResponse data);
}
