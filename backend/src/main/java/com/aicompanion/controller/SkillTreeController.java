package com.aicompanion.controller;

import com.aicompanion.common.Result;
import com.aicompanion.service.StudentSkillService;
import com.aicompanion.vo.SkillTreeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillTreeController {

    private final StudentSkillService studentSkillService;

    @GetMapping("/tree")
    public Result<List<SkillTreeVO>> getTree(
            @RequestParam(value = "category", required = false) String category) {
        Long userId = com.aicompanion.common.UserContext.getUserId();
        List<SkillTreeVO> tree = studentSkillService.getSkillTreeByCategory(userId, category);
        return Result.success(tree);
    }

    @PostMapping("/toggle/{id}")
    public Result<Void> toggleSkillStatus(@PathVariable("id") Long skillId) {
        Long userId = com.aicompanion.common.UserContext.getUserId();
        studentSkillService.toggleSkillStatus(userId, skillId);
        return Result.success();
    }

    @GetMapping("/categories")
    public Result<List<java.util.Map<String, String>>> getCategories() {
        List<java.util.Map<String, String>> categories = studentSkillService.getCategories();
        return Result.success(categories);
    }
}
