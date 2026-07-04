package com.aicompanion.service.impl;

import com.aicompanion.entity.SkillTree;
import com.aicompanion.entity.UserSkill;
import com.aicompanion.mapper.SkillTreeMapper;
import com.aicompanion.mapper.UserSkillMapper;
import com.aicompanion.service.StudentSkillService;
import com.aicompanion.vo.SkillTreeVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

@Service
@RequiredArgsConstructor
public class StudentSkillServiceImpl extends ServiceImpl<SkillTreeMapper, SkillTree> implements StudentSkillService {

    private final SkillTreeMapper skillTreeMapper;
    private final UserSkillMapper userSkillMapper;

    @Override
    public List<SkillTreeVO> getFullTree(Long userId) {
        return getSkillTreeByCategory(userId, null);
    }

    @Override
    public List<SkillTreeVO> getSkillTreeByCategory(String category) {
        return getSkillTreeByCategory(null, category);
    }

    @Override
    public List<SkillTreeVO> getSkillTreeByCategory(Long userId, String category) {
        if (userId == null) {
            throw new RuntimeException("未登录或 Token 无效");
        }

        // 1. 直接执行连表查询，获取带有 status 的扁平节点列表
        List<SkillTreeVO> skillVOs = skillTreeMapper.selectSkillTreeWithStatus(userId, category);

        // 2. 组装成树状结构
        return buildTree(skillVOs);
    }

    @Override
    public void toggleSkillStatus(Long userId, Long skillId) {
        LambdaQueryWrapper<UserSkill> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserSkill::getUserId, userId).eq(UserSkill::getSkillId, skillId);
        
        UserSkill userSkill = userSkillMapper.selectOne(queryWrapper);
        
        if (userSkill == null) {
            // 不存在记录，从未开始(0)变为学习中(1)
            userSkill = UserSkill.builder()
                    .userId(userId)
                    .skillId(skillId)
                    .status(1)
                    .build();
            userSkillMapper.insert(userSkill);
        } else {
            // 状态流转: 1 -> 2, 2 -> 0, (或者容错 0 -> 1)
            int currentStatus = userSkill.getStatus() == null ? 0 : userSkill.getStatus();
            int nextStatus = (currentStatus + 1) % 3;
            
            // 构造更新 Wrapper，精准锁定 user_id 和 skill_id
            LambdaUpdateWrapper<UserSkill> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(UserSkill::getUserId, userId)
                         .eq(UserSkill::getSkillId, skillId)
                         .set(UserSkill::getStatus, nextStatus)
                         .set(UserSkill::getLearnedAt, nextStatus == 2 ? LocalDateTime.now() : null);
                         
            userSkillMapper.update(null, updateWrapper);
            
            // Generate a learning record when a skill is mastered
            if (nextStatus == 2) {
                com.aicompanion.entity.SkillTree skill = skillTreeMapper.selectById(skillId);
                if (skill != null) {
                    com.aicompanion.entity.LearningRecord record = com.aicompanion.entity.LearningRecord.builder()
                            .userId(userId)
                            .skillName(skill.getName())
                            .content("通过APP端技能树点亮了【" + skill.getName() + "】技能。")
                            .duration(new java.util.Random().nextInt(60) + 30) // Random duration 30-90 min
                            .build();
                    com.aicompanion.common.SpringContextHolder.getBean(com.aicompanion.mapper.LearningRecordMapper.class).insert(record);
                }
            }
        }
    }

    private List<SkillTreeVO> buildTree(List<SkillTreeVO> allSkillVOs) {
        // 1. 筛选出所有的根节点（parentId 为 0 或 null）
        List<SkillTreeVO> rootNodes = allSkillVOs.stream()
                .filter(vo -> vo.getParentId() == null || vo.getParentId() == 0)
                .collect(Collectors.toList());

        // 2. 遍历根节点，递归组装子节点
        for (SkillTreeVO rootNode : rootNodes) {
            buildChildNodes(rootNode, allSkillVOs);
        }

        return rootNodes;
    }

    private void buildChildNodes(SkillTreeVO parentNode, List<SkillTreeVO> allSkillVOs) {
        // 找到当前节点的所有子节点
        List<SkillTreeVO> childrenNodes = allSkillVOs.stream()
                .filter(vo -> parentNode.getId().equals(vo.getParentId()))
                .collect(Collectors.toList());

        // 将子节点设置到当前节点中
        parentNode.setChildren(childrenNodes);

        // 递归处理子节点的子节点
        for (SkillTreeVO childNode : childrenNodes) {
            buildChildNodes(childNode, allSkillVOs);
        }
    }

    @Override
    public List<java.util.Map<String, String>> getCategories() {
        List<java.util.Map<String, Object>> list = skillTreeMapper.selectCategories();
        List<java.util.Map<String, String>> result = new java.util.ArrayList<>();
        
        java.util.Map<String, String> all = new java.util.HashMap<>();
        all.put("name", "ALL");
        all.put("code", "ALL");
        result.add(all);
        
        for (java.util.Map<String, Object> map : list) {
            java.util.Map<String, String> item = new java.util.HashMap<>();
            item.put("name", (String) map.get("name"));
            item.put("code", (String) map.get("category"));
            result.add(item);
        }
        return result;
    }

    @Override
    public void addSkillNode(SkillTree skillTree) {
        if (skillTree.getParentId() == null) {
            skillTree.setParentId(0L);
        }
        this.save(skillTree);
    }

    @Override
    public void updateSkillNode(SkillTree skillTree) {
        this.updateById(skillTree);
    }

    @Override
    public void deleteSkillNode(Long id) {
        // Recursive delete children
        deleteChildren(id);
        this.removeById(id);
    }

    private void deleteChildren(Long parentId) {
        LambdaQueryWrapper<SkillTree> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkillTree::getParentId, parentId);
        List<SkillTree> children = this.list(queryWrapper);
        for (SkillTree child : children) {
            deleteChildren(child.getId());
            this.removeById(child.getId());
        }
    }
}
