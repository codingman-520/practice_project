package com.aicompanion.service;

import com.aicompanion.entity.LearningRecord;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface LearningRecordService extends IService<LearningRecord> {
    Page<LearningRecord> getRecordPage(Long userId, String skillName, Integer page, Integer size);
    void deleteRecord(Long id);
}
