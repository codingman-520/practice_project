package com.aicompanion.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewQuestionVO {
    private Long sessionId;
    private Long questionId;
    private String questionText;
    
    // 当前是第几题（例如 1、2、3）
    private Integer currentIndex;
    
    // 面试总题数
    private Integer totalCount;
    
    // 是否已完成所有答题
    private Boolean isCompleted;
}
