package com.planner.service.search;

import com.planner.dtos.ServiceResult;

import java.util.List;
import java.util.Map;

public interface SearchService {

    ServiceResult<List<Map<String, Object>>> search(String query, List<String> types);
}
