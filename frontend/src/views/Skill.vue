<template>
  <div class="skill-container">
    <!-- Toolbar -->
    <el-card class="toolbar-card">
      <div class="toolbar-content">
        <el-form :inline="true" class="demo-form-inline">
          <el-form-item label="快速筛选分类">
            <el-select v-model="filterCategory" placeholder="显示全部" clearable style="width: 180px;">
              <el-option label="显示全部" value="" />
              <el-option label="前端开发" value="FRONTEND" />
              <el-option label="后端开发" value="BACKEND" />
              <el-option label="数据库" value="DATABASE" />
              <el-option label="运维部署" value="DEVOPS" />
              <el-option label="职业软技能" value="SOFT_SKILLS" />
            </el-select>
          </el-form-item>
        </el-form>
        <el-button type="success" :icon="Plus" @click="openAddDialog(0)">新增根技能</el-button>
      </div>
    </el-card>

    <!-- Tree Body -->
    <el-card class="tree-card" v-loading="loading">
      <el-tree
        :data="filteredTreeData"
        node-key="id"
        default-expand-all
        :expand-on-click-node="false"
        :props="defaultProps"
      >
        <template #default="{ node, data }">
          <span class="custom-tree-node">
            <span class="node-left">
              <span class="node-name">{{ data.name }}</span>
              <el-tag size="small" :type="getCategoryTag(data.category)" class="node-tag">
                {{ getCategoryName(data.category) }}
              </el-tag>
            </span>
            <span class="node-right">
              <el-button link type="primary" size="small" @click="openAddDialog(data.id)">添加子技能</el-button>
              <el-button link type="primary" size="small" @click="openEditDialog(data)">编辑</el-button>
              <el-button link type="danger" size="small" @click="handleDelete(data.id)">删除</el-button>
            </span>
          </span>
        </template>
      </el-tree>
    </el-card>

    <!-- Add/Edit Dialog -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑技能节点' : '新增技能节点'"
      width="500px"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="技能名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入技能名称" />
        </el-form-item>
        <el-form-item label="归属分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择归属分类" style="width: 100%">
            <el-option label="前端开发" value="FRONTEND" />
            <el-option label="后端开发" value="BACKEND" />
            <el-option label="数据库" value="DATABASE" />
            <el-option label="运维部署" value="DEVOPS" />
            <el-option label="职业软技能" value="SOFT_SKILLS" />
          </el-select>
        </el-form-item>
        <el-form-item label="技能简述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入技能简述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { Plus } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  getSkillTree,
  addSkillNode,
  updateSkillNode,
  deleteSkillNode
} from '../api/skill';

const loading = ref(false);
const treeData = ref([]);
const filterCategory = ref('');

const dialogVisible = ref(false);
const isEdit = ref(false);
const submitLoading = ref(false);
const currentParentId = ref(0);
const currentEditId = ref(null);

const formRef = ref(null);
const form = ref({
  name: '',
  category: 'FRONTEND',
  description: ''
});

const rules = {
  name: [{ required: true, message: '请输入技能名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择归属分类', trigger: 'change' }]
};

const defaultProps = {
  children: 'children',
  label: 'name'
};

const fetchTree = async () => {
  loading.value = true;
  try {
    const res = await getSkillTree();
    treeData.value = res.data;
  } catch (error) {
    ElMessage.error('加载技能树失败');
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchTree();
});

// Category filtering on the tree data
const filteredTreeData = computed(() => {
  if (!filterCategory.value) return treeData.value;
  return treeData.value.filter(node => node.category === filterCategory.value);
});

const getCategoryTag = (category) => {
  switch (category) {
    case 'FRONTEND': return '';
    case 'BACKEND': return 'success';
    case 'DATABASE': return 'warning';
    case 'DEVOPS': return 'danger';
    case 'SOFT_SKILLS': return 'info';
    default: return 'info';
  }
};

const getCategoryName = (category) => {
  switch (category) {
    case 'FRONTEND': return '前端';
    case 'BACKEND': return '后端';
    case 'DATABASE': return '数据库';
    case 'DEVOPS': return '运维';
    case 'SOFT_SKILLS': return '软技能';
    default: return '未知';
  }
};

const openAddDialog = (parentId) => {
  isEdit.value = false;
  currentParentId.value = parentId;
  dialogVisible.value = true;
};

const openEditDialog = (nodeData) => {
  isEdit.value = true;
  currentEditId.value = nodeData.id;
  form.value = {
    name: nodeData.name,
    category: nodeData.category,
    description: nodeData.description
  };
  dialogVisible.value = true;
};

const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields();
  }
  form.value = {
    name: '',
    category: 'FRONTEND',
    description: ''
  };
  currentParentId.value = 0;
  currentEditId.value = null;
};

const handleSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return;
    submitLoading.value = true;
    try {
      if (isEdit.value) {
        await updateSkillNode(currentEditId.value, form.value);
        ElMessage.success('更新技能成功');
      } else {
        await addSkillNode(currentParentId.value, form.value);
        ElMessage.success('新增技能成功');
      }
      dialogVisible.value = false;
      fetchTree();
    } catch (error) {
      ElMessage.error(isEdit.value ? '更新失败' : '新增失败');
    } finally {
      submitLoading.value = false;
    }
  });
};

const handleDelete = (id) => {
  ElMessageBox.confirm(
    '删除此技能将级联清除其所有的子技能分支，是否确定删除？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    loading.value = true;
    try {
      await deleteSkillNode(id);
      ElMessage.success('删除成功');
      fetchTree();
    } catch (error) {
      ElMessage.error('删除失败');
      loading.value = false;
    }
  }).catch(() => {});
};
</script>

<style scoped>
.skill-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.toolbar-card {
  border-radius: 8px;
}

.toolbar-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.toolbar-content .el-form-item {
  margin-bottom: 0;
}

.tree-card {
  border-radius: 8px;
  min-height: 400px;
}

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}

.node-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.node-name {
  font-weight: 500;
}

.node-tag {
  font-weight: normal;
}

.node-right {
  display: flex;
  gap: 10px;
}
</style>
