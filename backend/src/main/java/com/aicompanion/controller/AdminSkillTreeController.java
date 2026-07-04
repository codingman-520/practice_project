package com.aicompanion.controller;

import com.aicompanion.common.Result;
import com.aicompanion.entity.SkillTree;
import com.aicompanion.service.StudentSkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class AdminSkillTreeController {

    private final StudentSkillService studentSkillService;

    @PostMapping
    public Result<SkillTree> addSkillNode(@RequestBody SkillTree skillTree) {
        studentSkillService.addSkillNode(skillTree);
        return Result.success(skillTree);
    }

    @PutMapping("/{id}")
    public Result<Void> updateSkillNode(@PathVariable("id") Long id, @RequestBody SkillTree skillTree) {
        skillTree.setId(id);
        studentSkillService.updateSkillNode(skillTree);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteSkillNode(@PathVariable("id") Long id) {
        studentSkillService.deleteSkillNode(id);
        return Result.success();
    }
}
