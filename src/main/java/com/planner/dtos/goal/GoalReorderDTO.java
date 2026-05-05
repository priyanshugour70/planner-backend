package com.planner.dtos.goal;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalReorderDTO {

    @NotEmpty(message = "Goal order list cannot be empty")
    private List<GoalOrderItem> goals;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GoalOrderItem {
        private String uuid;
        private Integer number;
    }
}
