package com.binarybrains.blogpersonal.common;

import java.util.Map;
import lombok.Builder;

@Builder(toBuilder = true)
public record FieldsErrorResponse(String error, String message, Map<String, String> fields) {}
