# API 接口文档 (API Documentation)

本系统采用 RESTful 风格的 API 接口设计，所有请求均通过 HTTP/HTTPS 协议进行。

## 1. 统一响应格式
所有的接口响应均遵循以下标准 JSON 格式：
```json
{
  "code": 200, 
  "message": "success",
  "data": { ... } // 或 []
}
```
*   `200`: 请求成功
*   `401`: Token 缺失或失效（需重新登录）
*   `403`: 角色权限不足
*   `500`: 服务器内部异常

## 2. 鉴权机制
除了登录、注册接口外，所有请求必须在 HTTP Header 中携带 `Authorization` 字段：
```http
Authorization: Bearer <Your-JWT-Token>
```

---

## 3. 核心接口清单

### 3.1 用户认证与管理
*   **管理端登录**: `POST /api/auth/login/admin`
*   **学生端登录**: `POST /api/auth/login/student`
*   **获取个人信息**: `GET /api/user/profile`
*   **获取个人统计**: `GET /api/user/stats`
*   **[管理端] 分页获取用户列表**: `GET /api/users`
*   **[管理端] 修改用户角色**: `PUT /api/users/{id}/role`

### 3.2 技能树模块
*   **获取完整技能树**: `GET /api/skills/tree`
*   **获取技能分类列表**: `GET /api/skills/categories`
*   **点亮/取消技能**: `POST /api/skills/toggle/{id}`
    *   *说明*: 当子技能被点亮时，后端会自动在 `learning_record` 插入一条记录。
*   **[管理端] 新增技能节点**: `POST /api/skills`
*   **[管理端] 更新技能节点**: `PUT /api/skills/{id}`
*   **[管理端] 删除技能节点**: `DELETE /api/skills/{id}`

### 3.3 学习记录模块
*   **[管理端] 分页获取学习记录**: `GET /api/records`
*   **[管理端] 删除学习记录**: `DELETE /api/records/{id}`

### 3.4 AI 辅导与模拟面试
*   **发送聊天消息**: `POST /api/ai/chat`
*   **启动模拟面试**: `POST /api/interview/start`
*   **提交面试答题**: `POST /api/interview/answer`
*   **获取面试历史记录**: `GET /api/interview/history`
*   **获取单次面试报告**: `GET /api/interview/report/{sessionId}`

### 3.5 管理端数据看板
*   **获取核心数据统计**: `GET /api/dashboard/stats`
*   **获取各技能类目掌握分布**: `GET /api/dashboard/category-distribution`
*   **获取 AI 每日调用趋势**: `GET /api/dashboard/ai-call-trend`
*   **获取近期学习动态**: `GET /api/dashboard/recent-activities`