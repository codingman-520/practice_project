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
            @RequestParam(required = false) Long userId,
            @RequestParam(value = "category", required = false) String category) {
        List<SkillTreeVO> tree = studentSkillService.getSkillTreeByCategory(userId, category);
        return Result.success(tree);
    }

    @PostMapping("/toggle/{id}")
    public Result<Void> toggleSkillStatus(@RequestParam Long userId, @PathVariable("id") Long skillId) {
        studentSkillService.toggleSkillStatus(userId, skillId);
        return Result.success();
    }
}
