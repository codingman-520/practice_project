package com.aicompanion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DifyChatRequestDTO {
    private String query;
    private String user;
    private String conversation_id;
    private Map<String, Object> inputs = new HashMap<>();
}
