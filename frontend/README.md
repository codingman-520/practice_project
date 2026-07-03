# AI伴学与职业成长平台 - Web管理后台

本项目是“AI伴学与职业成长平台”的官方 Web 管理后台，基于 Vue 3、Vite、Element Plus、ECharts 以及 Pinia 状态管理库构建。

## 核心功能

1. **数据总览 (Dashboard)**：
   - 4大核心统计指标卡片（总用户数、技能节点数、伴学记录数、AI累计调用量），带有动态悬浮效果。
   - 技能掌握度分布饼图（ECharts 渲染 5 大技能分类占比）。
   - 最近 7 天 AI 调用量面积趋势图。
   - 系统最近动态流水表格。

2. **用户管理 (User Management)**：
   - 账号角色多维搜索过滤（用户名模糊查询、角色筛选）。
   - 用户列表分页展示，支持不同角色标签高亮显示（管理员为红色，教师为蓝色，学生为绿色）。
   - 用户状态在线启用/禁用切换（ElSwitch）。
   - 用户角色实时修改与二次确认弹窗。

3. **技能树管理 (Skill Tree Management)**：
   - 树状结构节点嵌套展示（ElTree），支持快速筛选五大核心技能体系分类。
   - 节点自定义渲染，包含归属分类胶囊标签。
   - 技能节点的增、删、改以及子节点级联操作。
   - 删除节点的级联警告确认框。

4. **学习记录运维 (Learning Records)**：
   - 伴学日记日常流水列表，支持按用户 ID 和技能关键字进行模糊检索。
   - 文本溢出省略排版，鼠标悬停支持 tooltip 完整内容查看。
   - 记录时长时钟图标标识及行内删除二次确认警告。

5. **AI服务监控 (AI Service Monitor)**：
   - **对话模拟沙箱**：集成 AI 智能伴学助手会话，包含呼吸灯在线指示、气泡气泡流消息卡片、AI 检索解析骨架屏动画等。
   - **模拟面试沙箱**：选择技能方向快速触发模拟面试测试，显示最终评估分数和用时。
   - **面试历史列表**：展示全员模拟面试流水。
   - **AI诊断报告**：点击查看历史面试详情，弹出并渲染结构化 Markdown 诊断分析报告。

## 目录结构

```text
src/
├── api/             # 业务服务接口封装层 (Mock 异步数据源)
│   ├── auth.js      # 身份验证及登录 Mock
│   ├── dashboard.js # 仪表盘数据看板 Mock
│   ├── user.js      # 用户信息 CRUD Mock
│   ├── skill.js     # 技能树 CRUD Mock
│   ├── record.js    # 学习记录查询 Mock
│   └── ai.js        # AI 对话与模拟面试 Mock
├── components/      # 公共 UI 组件
│   └── Layout.vue   # 系统通用侧边栏/顶栏布局框架
├── store/           # Pinia 全局状态管理
│   ├── app.js       # 系统配置 (侧边栏收缩等)
│   └── user.js      # 用户 Token 及信息持久化
├── views/           # 页面级视图组件
│   ├── Login.vue      # 渐变背景登录页
│   ├── Dashboard.vue  # 数据大屏可视化看板
│   ├── User.vue       # 用户管理
│   ├── Skill.vue      # 技能树维护
│   ├── Record.vue     # 伴学记录审计
│   └── AiMonitor.vue  # AI 沙箱与面试诊断
├── App.vue          # 主入口组件
├── main.js          # App 引导文件
├── router/          # Vue Router 4 路由配置文件
│   └── index.js     # 路由配置与 JWT 路由守卫
└── style.css        # 全局公共样式重置
```

## 本地开发运行

### 1. 安装依赖

```bash
npm install
```

### 2. 启动开发服务器

```bash
npm run dev
```

### 3. 构建打包生产环境

```bash
npm run build
```

## 后续对接真实后端说明

当后端接口就绪后，无需修改任何 views 中的业务逻辑代码，只需打开 `src/api/` 下的对应文件，引入 `src/utils/request.js` (封装了全局 Bearer Token 拦截器的 Axios 实例)，将 Promise 结构替换为真实的 `request.get()` / `request.post()` 网络请求即可：

```javascript
import request from '../utils/request';

export const getDashboardStats = async () => {
  const response = await request.get('/api/dashboard');
  return response.data;
};
```
