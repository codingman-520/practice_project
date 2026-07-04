package com.aicompanion.controller;

import com.aicompanion.common.Result;
import com.aicompanion.entity.LearningRecord;
import com.aicompanion.service.LearningRecordService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class AdminRecordController {

    private final LearningRecordService learningRecordService;

    @GetMapping
    public Result<Map<String, Object>> getRecordList(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "skillName", required = false) String skillName,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        
        Page<LearningRecord> recordPage = learningRecordService.getRecordPage(userId, skillName, page, size);
        return Result.success(Map.of(
                "list", recordPage.getRecords(),
                "total", recordPage.getTotal()
        ));
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteRecord(@PathVariable("id") Long id) {
        learningRecordService.deleteRecord(id);
        return Result.success();
    }
}
