package com.aicompanion.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewReportVO {
    private Long sessionId;
    private String jobPosition;
    private Integer score;
    private String feedback;
}
