package com.binarybrains.blogpersonal.common;

import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
public record ErrorResponse(String message, List<Map<String, String>> errors, String timestamp) {}
