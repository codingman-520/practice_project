package com.aicompanion.service;

import com.aicompanion.entity.SkillTree;
import com.aicompanion.vo.SkillTreeVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface StudentSkillService extends IService<SkillTree> {
    List<SkillTreeVO> getFullTree(Long userId);
    
    // 为了兼容原有逻辑并支持状态，我们将 userId 也传入
    List<SkillTreeVO> getSkillTreeByCategory(Long userId, String category);
    
    // 满足需求文档中定义的无 userId 签名的方法（重载）
    List<SkillTreeVO> getSkillTreeByCategory(String category);
    
    void toggleSkillStatus(Long userId, Long skillId);

    List<Map<String, String>> getCategories();

    void addSkillNode(SkillTree skillTree);
    void updateSkillNode(SkillTree skillTree);
    void deleteSkillNode(Long id);
}
