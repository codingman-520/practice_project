# 项目部署说明 (Deployment Guide)

本指南介绍如何在本地环境中快速启动并运行“AI伴学与职业成长平台”的三个端（后端、Web端、移动端）。

## 1. 数据库准备
1. 安装并启动 MySQL 8.0。
2. 登录 MySQL 并创建数据库：
   ```sql
   CREATE DATABASE IF NOT EXISTS `ai_companion` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
3. 后端服务启动时，会自动读取 `backend/src/main/resources/db/schema.sql` 完成表结构创建及初始数据导入。
4. *注意*：请确保 `backend/src/main/resources/application.yml` 中的数据库用户名（如 `root`）和密码与您本地的 MySQL 环境一致。

## 2. 启动 Spring Boot 后端服务
1. 使用 IntelliJ IDEA 或其他 Java IDE 打开 `practice-project/practice_project/backend` 目录。
2. 刷新 Maven 依赖（`mvn clean install -DskipTests`）。
3. 找到主启动类 `com.aicompanion.AiCompanionApplication`，直接运行。
4. 后端服务将默认运行在 `http://localhost:8080`。

## 3. 启动 Vue 3 Web 管理后台
1. 打开终端，进入前端目录：
   ```bash
   cd practice-project/practice_project/frontend
   ```
2. 安装依赖包：
   ```bash
   npm install
   ```
3. 启动本地开发服务器：
   ```bash
   npm run dev
   ```
4. 在浏览器中打开 `http://localhost:5173/`。
5. 登录凭证（初始数据自动生成）：
   - 账号：`admin`
   - 密码：`admin123`

## 4. 启动 HarmonyOS 移动端
1. 打开 **DevEco Studio**。
2. 导入（Open Project）根目录下的 `Harmony` 文件夹。
3. 等待 Gradle/Hvigor 自动同步工程依赖。
4. 点击工具栏的 `Run` 按钮，启动本地模拟器或将 Hap 包推送到真机。
5. 移动端默认的网络请求地址指向 `http://10.0.2.2:8080`（即模拟器访问宿主机的标准地址）。
6. 登录凭证（初始数据自动生成）：
   - 账号：`student01`
   - 密码：`student123`

## 5. Dify AI 引擎配置说明
系统中的“AI 对话”和“模拟面试”强依赖于 Dify 的大模型 API。
在 `backend/src/main/resources/application.yml` 中：
```yaml
dify:
  chat:
    api-key: app-xxxxxxxxxxx # 替换为您的 Chatflow API Key
  workflow:
    api-key: app-xxxxxxxxxxx # 替换为您的 Workflow API Key
```
如果您本地没有配置有效的 Dify Key，系统后端已经做好了**兜底容错处理**，会自动返回预设的 Mock 文本数据以保障测试流程不被中断。