let interviewHistory = [
  { id: 2001, username: 'student01', jobPosition: '前端开发工程师', status: 'COMPLETED', score: 85, feedback: '前端基础扎实，但在高并发渲染优化和 Webpack 构建配置方面还需要加强。', createTime: '2026-07-03 14:00:00' },
  { id: 2002, username: 'student02', jobPosition: 'Java 后端开发', status: 'COMPLETED', score: 78, feedback: '对于 JVM 内存模型和多线程并发控制理解较好，但数据库事务隔离级别及索引底层 B+ 树的原理掌握一般。', createTime: '2026-07-03 11:30:00' },
  { id: 2003, username: 'student03', jobPosition: '运维部署工程师', status: 'COMPLETED', score: 92, feedback: '非常优秀，熟悉 Docker 容器化及 K8s 集群部署，CI/CD 流水线搭建思路清晰。', createTime: '2026-07-02 16:00:00' },
  { id: 2004, username: 'student04', jobPosition: '数据库管理员', status: 'IN_PROGRESS', score: 0, feedback: '', createTime: '2026-07-02 09:20:00' },
  { id: 2005, username: 'student05', jobPosition: '软技能专项', status: 'COMPLETED', score: 80, feedback: '沟通流畅，逻辑表述清晰，STAR 原则项目叙事完整。', createTime: '2026-07-01 15:40:00' }
];

export const sendChatMessage = (message) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      let reply = `关于您的问题 "${message}"，AI 伴学助手建议：在前端架构设计中，要时刻注意数据流动清晰性。推荐使用 Composition API 来聚合相关逻辑，并利用 Axios 统一封装网络请求。此外，配合 Pinia 等轻量化状态库可以很好地实现状态共享。`;
      if (message.toLowerCase().includes('vue')) {
        reply = `Vue 3 引入的 Composition API 极大地改善了代码的可维护性。建议了解一下 ref 与 reactive 的核心工作原理（基于 ES6 Proxy 的属性拦截）。同时，熟练掌握 Vue Router 4 的路由钩子和动态路由对后台开发至关重要。`;
      } else if (message.toLowerCase().includes('docker')) {
        reply = `Docker 容器技术在现代 DevOps 中是不可或缺的。对于前端开发者，通常将打包生成的 dist 文件夹放置在 Nginx 镜像中进行轻量化部署。这对于构建 CI/CD 自动化流水线（如 Jenkins 或 GitHub Actions）非常方便。`;
      }
      resolve({
        code: 200,
        message: 'success',
        data: {
          reply,
          timestamp: new Date().toLocaleTimeString()
        }
      });
    }, 1200); // Simulated network delay
  });
};

export const startMockInterview = (skillId) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      const scores = [78, 85, 89, 92, 96];
      const randomScore = scores[Math.floor(Math.random() * scores.length)];
      const duration = Math.floor(Math.random() * 10) + 15; // 15-25 minutes
      
      const newInterview = {
        id: Date.now(),
        username: 'student01',
        jobPosition: skillId === 'FRONTEND' ? '前端开发工程师' : (skillId === 'BACKEND' ? '后端开发工程师' : '全栈开发工程师'),
        status: 'COMPLETED',
        score: randomScore,
        feedback: `技术评估完成！综合得分 ${randomScore} 分，评估时长 ${duration} 分钟。`,
        createTime: new Date().toLocaleString()
      };
      
      interviewHistory.unshift(newInterview);
      
      resolve({
        code: 200,
        message: 'success',
        data: newInterview
      });
    }, 1500);
  });
};

export const getInterviewHistory = () => {
  return new Promise((resolve) => {
    setTimeout(() => {
      resolve({
        code: 200,
        message: 'success',
        data: [...interviewHistory]
      });
    }, 400);
  });
};

export const getInterviewReport = (id) => {
  return new Promise((resolve) => {
    setTimeout(() => {
      const interview = interviewHistory.find(i => i.id === id) || interviewHistory[0];
      const markdownReport = `
# AI 模拟面试诊断评估报告

### 基础数据
- **候选人**：${interview.username}
- **评估岗位**：${interview.jobPosition}
- **面试时间**：${interview.createTime}
- **综合得分**：**${interview.score}分**

---

### 技术维度分析
1. **基础理论掌握度**（权重 40%）：**85/100**
   - 能够准确阐述相关技术栈的基本机制。
   - 细节描述较完整，基础功底扎实。
2. **实际工程应用能力**（权重 40%）：**80/100**
   - 具备独立搭建基本功能模块的能力，但在遇到大规模复杂架构时，思路不够开阔。
3. **架构与性能优化意识**（权重 20%）：**75/100**
   - 在高并发、缓存击穿、前端打包体积优化等高级特性上表现一般，仍需重点加强。

---

### AI 诊断建议
- **优势**：学习主动性强，基本语法理解深透，能够迅速根据面试题目做出正确的答案回应。
- **不足**：缺乏复杂大中型系统的实战沉淀，对于性能调优的经验较少。
- **推荐学习路径**：建议阅读 [MySQL 慢查询优化] 以及 [Docker 多阶段构建与 Kubernetes 指引]。
`;
      resolve({
        code: 200,
        message: 'success',
        data: {
          report: markdownReport
        }
      });
    }, 400);
  });
};
