package com.planner.controllers.search;

import com.planner.dtos.APIResponse;
import com.planner.dtos.ServiceResult;
import com.planner.service.search.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Tag(name = "Search", description = "Global search across all entities")
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    @Operation(summary = "Search across goals, tasks, notes, habits, journals, and reminders")
    public ResponseEntity<APIResponse<List<Map<String, Object>>>> search(
            @RequestParam String query,
            @RequestParam(required = false) List<String> types) {
        ServiceResult<List<Map<String, Object>>> result = searchService.search(query, types);
        return toApiResponse(result, "Search results retrieved successfully");
    }

    private <T> ResponseEntity<APIResponse<T>> toApiResponse(ServiceResult<T> result, String message) {
        if (result.isSuccess()) {
            return ResponseEntity.ok(APIResponse.success(result.getData(), message));
        }
        return ResponseEntity.status(result.getStatus())
                .body(APIResponse.failure(result.getErrors(), result.getStatus(), message));
    }
}
