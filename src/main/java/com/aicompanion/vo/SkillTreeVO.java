package com.aicompanion.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillTreeVO {
    private Long id;
    private String name;
    private String description;
    private Long parentId;
    private String category;
    
    private Integer status;
    
    @Builder.Default
    private List<SkillTreeVO> children = new ArrayList<>();
}
