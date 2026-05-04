package com.planner.controllers.sync;

import com.planner.dtos.APIResponse;
import com.planner.dtos.ServiceResult;
import com.planner.dtos.req.sync.SyncRequest;
import com.planner.dtos.res.sync.FullSyncResponse;
import com.planner.dtos.res.sync.SyncResponse;
import com.planner.service.sync.SyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sync")
@RequiredArgsConstructor
@Tag(name = "Sync", description = "Data synchronization")
public class SyncController {

    private final SyncService syncService;

    @PostMapping("/push")
    @Operation(summary = "Push local changes to the server")
    public ResponseEntity<APIResponse<SyncResponse>> push(@Valid @RequestBody SyncRequest request) {
        ServiceResult<SyncResponse> result = syncService.pushChanges(request);
        return toApiResponse(result, "Changes pushed successfully");
    }

    @GetMapping("/pull")
    @Operation(summary = "Pull all data from server")
    public ResponseEntity<APIResponse<FullSyncResponse>> pull() {
        ServiceResult<FullSyncResponse> result = syncService.pullAllData();
        return toApiResponse(result, "Data pulled successfully");
    }

    @PostMapping("/full")
    @Operation(summary = "Full sync upload — import complete AppData snapshot")
    public ResponseEntity<APIResponse<Void>> fullSync(@Valid @RequestBody FullSyncResponse appData) {
        ServiceResult<Void> result = syncService.fullSync(appData);
        return toApiResponse(result, "Full sync completed successfully");
    }

    private <T> ResponseEntity<APIResponse<T>> toApiResponse(ServiceResult<T> result, String message) {
        if (result.isSuccess()) {
            return ResponseEntity.ok(APIResponse.success(result.getData(), message));
        }
        return ResponseEntity.status(result.getStatus())
                .body(APIResponse.failure(result.getErrors(), result.getStatus(), message));
    }
}
