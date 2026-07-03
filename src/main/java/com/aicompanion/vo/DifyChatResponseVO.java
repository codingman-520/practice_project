package com.aicompanion.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DifyChatResponseVO {
    private String answer;
    private String conversation_id;
    private Long create_at;
}
