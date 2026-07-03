let skillTree = [
  {
    id: 100,
    name: '前端开发 (FRONTEND)',
    category: 'FRONTEND',
    description: 'Web 前端核心技术栈',
    children: [
      {
        id: 101,
        name: 'Vue 3',
        category: 'FRONTEND',
        description: 'Vue 3 渐进式 JavaScript 框架',
        children: [
          { id: 1011, name: 'Composition API', category: 'FRONTEND', description: '组合式 API 核心用法', children: [] },
          { id: 1012, name: 'Pinia & Vue Router', category: 'FRONTEND', description: '全局状态管理与路由导航', children: [] }
        ]
      },
      {
        id: 102,
        name: 'TypeScript',
        category: 'FRONTEND',
        description: '微软开发的强类型 JavaScript 超集',
        children: []
      }
    ]
  },
  {
    id: 200,
    name: '后端开发 (BACKEND)',
    category: 'BACKEND',
    description: '服务器端核心技术与微服务',
    children: [
      {
        id: 201,
        name: 'Spring Boot 3',
        category: 'BACKEND',
        description: '基于 Java 的企业级快速开发框架',
        children: []
      },
      {
        id: 202,
        name: 'Spring AI',
        category: 'BACKEND',
        description: '集成大语言模型(LLM)的 AI 应用开发组件',
        children: []
      }
    ]
  },
  {
    id: 300,
    name: '数据库 (DATABASE)',
    category: 'DATABASE',
    description: '关系型与非关系型数据存储及优化',
    children: [
      { id: 301, name: 'MySQL', category: 'DATABASE', description: '最流行的开源关系型数据库管理系统', children: [] },
      { id: 302, name: 'Redis', category: 'DATABASE', description: '内存键值对数据缓存系统', children: [] }
    ]
  },
  {
    id: 400,
    name: '运维部署 (DEVOPS)',
    category: 'DEVOPS',
    description: '持续集成与容器化集群部署',
    children: [
      { id: 401, name: 'Docker', category: 'DEVOPS', description: '开源容器化引擎', children: [] },
      { id: 402, name: 'Jenkins & CI/CD', category: 'DEVOPS', description: '流水线自动化集成部署', children: [] }
    ]
  },
  {
    id: 500,
    name: '职业软技能 (SOFT_SKILLS)',
    category: 'SOFT_SKILLS',
    description: '求职、面试与团队沟通协作能力',
    children: [
      { id: 501, name: '简历优化', category: 'SOFT_SKILLS', description: '撰写出色的求职简历', children: [] },
      { id: 502, name: '面试技巧', category: 'SOFT_SKILLS', description: '技术答辩与HR沟通话术', children: [] }
    ]
  }
];

const insertNode = (nodes, parentId, newNode) => {
  if (parentId === 0 || parentId === null || parentId === '') {
    nodes.push(newNode);
    return true;
  }
  for (let node of nodes) {
    if (node.id === parentId) {
      if (!node.children) node.children = [];
      node.children.push(newNode);
      return true;
    }
    if (node.children && node.children.length > 0) {
      if (insertNode(node.children, parentId, newNode)) return true;
    }
  }
  return false;
};

const updateNode = (nodes, id, data) => {
  for (let node of nodes) {
    if (node.id === id) {
      node.name = data.name;
      node.category = data.category;
      node.description = data.description;
      return true;
    }
    if (node.children && node.children.length > 0) {
      if (updateNode(node.children, id, data)) return true;
    }
  }
  return false;
};

const deleteNode = (nodes, id) => {
  for (let i = 0; i < nodes.length; i++) {
    if (nodes[i].id === id) {
      nodes.splice(i, 1);
      return true;
    }
    if (nodes[i].children && nodes[i].children.length > 0) {
      if (deleteNode(nodes[i].children, id)) return true;
    }
  }
  return false;
};

export const getSkillTree = () => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        code: 200,
        message: 'success',
        data: JSON.parse(JSON.stringify(skillTree))
      });
    }, 400);
  });
};

export const addSkillNode = (parentId, nodeData) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      const newNode = {
        id: Date.now(),
        name: nodeData.name,
        category: nodeData.category,
        description: nodeData.description || '',
        children: []
      };
      insertNode(skillTree, parentId, newNode);
      resolve({
        code: 200,
        message: 'success',
        data: newNode
      });
    }, 400);
  });
};

export const updateSkillNode = (id, nodeData) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      updateNode(skillTree, id, nodeData);
      resolve({
        code: 200,
        message: 'success'
      });
    }, 400);
  });
};

export const deleteSkillNode = (id) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      deleteNode(skillTree, id);
      resolve({
        code: 200,
        message: 'success'
      });
    }, 400);
  });
};
