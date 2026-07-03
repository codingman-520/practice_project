package com.aicompanion.mapper;

import com.aicompanion.entity.SkillTree;
import com.aicompanion.vo.SkillTreeVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SkillTreeMapper extends BaseMapper<SkillTree> {

    @Select("<script>" +
            "SELECT " +
            "    t.id, " +
            "    t.name, " +
            "    t.description, " +
            "    t.parent_id AS parentId, " +
            "    t.category, " +
            "    COALESCE(u.status, 0) AS status " +
            "FROM skill_tree t " +
            "LEFT JOIN user_skill u ON t.id = u.skill_id AND u.user_id = #{userId} " +
            "<where>" +
            "    <if test='category != null and category != \"\" and category != \"ALL\"'>" +
            "        UPPER(t.category) = UPPER(#{category}) " +
            "    </if>" +
            "</where>" +
            "</script>")
    List<SkillTreeVO> selectSkillTreeWithStatus(@Param("userId") Long userId, @Param("category") String category);

    @Select("SELECT name, category FROM skill_tree WHERE parent_id = 0")
    List<java.util.Map<String, Object>> selectCategories();
}
