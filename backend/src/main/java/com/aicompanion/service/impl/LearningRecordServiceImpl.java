package com.aicompanion.service.impl;

import com.aicompanion.entity.LearningRecord;
import com.aicompanion.mapper.LearningRecordMapper;
import com.aicompanion.service.LearningRecordService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LearningRecordServiceImpl extends ServiceImpl<LearningRecordMapper, LearningRecord> implements LearningRecordService {

    @Override
    public Page<LearningRecord> getRecordPage(Long userId, String skillName, Integer page, Integer size) {
        Page<LearningRecord> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<LearningRecord> queryWrapper = new LambdaQueryWrapper<>();
        
        if (userId != null) {
            queryWrapper.eq(LearningRecord::getUserId, userId);
        }
        if (StringUtils.hasText(skillName)) {
            queryWrapper.like(LearningRecord::getSkillName, skillName);
        }
        queryWrapper.orderByDesc(LearningRecord::getCreateTime);
        
        return this.page(pageParam, queryWrapper);
    }

    @Override
    public void deleteRecord(Long id) {
        this.removeById(id);
    }
}
