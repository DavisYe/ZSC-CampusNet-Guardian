# 校园网络报修系统

![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)
![License](https://img.shields.io/badge/license-MIT-green.svg)

## 目录

- [项目概述](#项目概述)
- [系统功能](#系统功能)
- [技术方案](#技术方案)
- [快速开始](#快速开始)
- [部署指南](#部署指南)
- [开发指南](#开发指南)
- [常见问题](#常见问题)
- [更新日志](#更新日志)
- [贡献指南](#贡献指南)

## 项目概述

校园网络报修系统是一个现代化的故障报修管理平台，主要特点：

- 🚀 高效的工单处理流程
- 💻 全终端自适应支持
- 🛡️ 完善的权限控制
- 📊 强大的数据分析
- 🔄 智能工单分配
- 📱 移动端优先设计

### 项目演示

[在这里添加系统截图或演示GIF]

## 系统功能

<details>
<summary>用户端功能</summary>

- 用户注册与登录
  - 手机号/学号注册
  - 密码登录与找回
  - JWT身份认证
- 故障报修
  - 故障类型选择
  - 图片上传
  - 预约维修
- 进度查询
  - 实时状态跟踪
  - 处理过程时间线
- 历史记录
  - 记录查询
  - 评价反馈
</details>

### 工作人员功能
<details>
<summary>工单接收与分配</summary>

- 新工单实时提醒
- 智能排班算法
  - 基于技能匹配
  - 考虑工作负载均衡
  - 支持组队修理
  - 动态调整优先级
- 手动调整分配
</details>

<details>
<summary>工单处理状态更新</summary>

- 状态流转（待处理、处理中、已上报、已完成）
- 添加上报说明（无法处理原因）
- 添加处理备注
- 上传维修证明
</details>

<details>
<summary>工单完成确认</summary>

- 用户确认完成
- 系统自动确认超时
- 生成维修报告
</details>

<details>
<summary>工单统计与报表</summary>

- 个人工作量统计
- 工单完成率分析
- 常见问题统计
</details>

### 管理员功能
<details>
<summary>用户管理</summary>

- 用户信息维护
  - 工号管理
    - 自动生成唯一工号
    - 工号权限管理
- 权限管理
- 安全审计
  - 操作日志审计
    - 完整操作记录
    - 敏感操作标记
    - 日志分析报告
  - 数据加密
    - 敏感数据加密存储
    - 传输加密（TLS）
    - 密钥轮换机制
- 招新管理
  - 招新申请提交
  - 多轮面试安排
    - 初试
    - 复试
    - 终面
  - 面试结果评估
  - 录用审批
  - 新成员培训
  - 工号分配
</details>

<details>
<summary>工作人员管理</summary>

- 排班规则：
  - 按区域进行排班
  - 每个区域设一名组长
  - 组长负责区域内的工单分配与协调
  - 所有组长与API职位对接
- 技能标签管理
- 绩效评估
</details>

<details>
<summary>系统配置</summary>

- 故障类型管理
- 通知管理
  - 邮件通知配置
    - SMTP服务器设置
    - 发件人信息配置
    - 邮件发送频率控制
  - 通知模板编辑
    - 支持HTML格式模板
    - 变量替换功能
    - 模板版本管理
- 系统参数设置
</details>

<details>
<summary>数据统计与分析</summary>

- 数据分析平台
  - 实时数据监控
    - 系统运行状态
    - 工单处理进度
    - 资源使用情况
  - 统计分析
    - 工单处理效率分析
    - 用户满意度统计
    - 系统使用情况报表
  - 预测分析
    - 故障趋势预测
    - 资源需求预测
    - 人员调度优化
  - 可视化报表
    - 自定义报表生成
    - 数据导出功能
    - 多维度分析
</details>

## 移动端适配

<details>
<summary>设备适配</summary>

- 响应式布局,自动适应不同屏幕尺寸
- 触摸区域优化,提升操作体验
- 字体与图片自适应调整
- 表格横向滚动支持
- 代码块优化显示
- 双栏布局(平板端)
- 支持触摸和键盘操作
</details>

<details>
<summary>技术实现</summary>

- 媒体查询(@media)断点适配
- viewport配置与缩放控制
- rem/em相对单位
- 触摸事件优化
- 图片自适应(max-width:100%)
</details>

<details>
<summary>兼容性支持</summary>

- iOS 9.0+
- Android 5.0+
- 主流平板设备(iPad/Surface等)
</details>

<details>
<summary>性能优化</summary>

- 资源优化
  - 图片懒加载与预加载
  - 自适应图片加载
  - 静态资源CDN加速
- 渲染优化
  - 虚拟列表
  - 骨架屏加载
  - 减少重绘重排
- 网络优化
  - Service Worker缓存
  - 请求合并与压缩
  - 离线功能支持
- 运行时优化
  - 代码分割(Code Splitting)
  - 组件懒加载
  - 内存使用优化
</details>

## 技术方案

### 技术架构

<details>
<summary>前端技术栈</summary>

- Next.js 13+
- React 18+
- Material-UI (MUI)
- TypeScript
- PWA支持
</details>

<details>
<summary>后端技术栈</summary>

- Spring Boot 3.0+
- Spring Security
- MyBatis Plus
- MySQL 8.0+
- Redis
- RabbitMQ
</details>

### 移动端适配

<details>
<summary>设备适配</summary>

- 响应式布局,自动适应不同屏幕尺寸
- 触摸区域优化,提升操作体验
- 字体与图片自适应调整
- 表格横向滚动支持
- 代码块优化显示
- 双栏布局(平板端)
- 支持触摸和键盘操作
</details>

<details>
<summary>技术实现</summary>

- 媒体查询(@media)断点适配
- viewport配置与缩放控制
- rem/em相对单位
- 触摸事件优化
- 图片自适应(max-width:100%)
</details>

<details>
<summary>兼容性支持</summary>

- iOS 9.0+
- Android 5.0+
- 主流平板设备(iPad/Surface等)
</details>

<details>
<summary>性能优化</summary>

- 资源优化
  - 图片懒加载与预加载
  - 自适应图片加载
  - 静态资源CDN加速
- 渲染优化
  - 虚拟列表
  - 骨架屏加载
  - 减少重绘重排
- 网络优化
  - Service Worker缓存
  - 请求合并与压缩
  - 离线功能支持
- 运行时优化
  - 代码分割(Code Splitting)
  - 组件懒加载
  - 内存使用优化
</details>

## 快速开始

### 环境要求

- Node.js 18+
- Java 17+
- MySQL 8.0+
- Redis 6.0+
- RabbitMQ 3.9+

### 本地开发

1. 克隆项目
```bash
git clone https://github.com/your-username/campus-repair.git
cd campus-repair
```

2. 安装依赖
```bash
# 前端依赖
cd frontend
npm install

# 后端依赖
cd ../backend
mvn install
```

3. 配置环境
```bash
# 复制配置文件
cp .env.example .env
# 修改配置文件中的数据库连接等信息
```

4. 启动服务
```bash
# 启动前端开发服务器
npm run dev

# 启动后端服务
mvn spring-boot:run
```

## 部署指南

### Docker部署

<details>
<summary>容器架构</summary>

- 应用容器
  - frontend: Next.js应用
  - backend: Spring Boot应用
- 数据容器
  - mysql: MySQL数据库
  - redis: Redis缓存
  - rabbitmq: 消息队列
- 反向代理
  - nginx: 请求转发与负载均衡
</details>

<details>
<summary>快速部署</summary>

1. 准备配置文件
```bash
cp docker-compose.yml.example docker-compose.yml
cp .env.example .env
```

2. 修改环境变量
```bash
vim .env
# 配置数据库连接等信息
```

3. 启动服务
```bash
docker-compose up -d
```

4. 查看服务状态
```bash
docker-compose ps
```
</details>

<details>
<summary>数据持久化</summary>

- Docker volumes配置
  ```yaml
  volumes:
    mysql_data:
    redis_data:
    rabbitmq_data:
  ```
- 持久化内容
  - 数据库数据
  - 日志文件
  - 配置文件
  - 上传文件
</details>

<details>
<summary>更新升级</summary>

- 滚动更新策略
  ```bash
  docker-compose up -d --no-deps --build <service_name>
  ```
- 数据库迁移
  - 使用Flyway管理版本
  - 自动执行迁移脚本
- 配置更新
  - 环境变量注入
  - 配置文件挂载
</details>

<details>
<summary>监控管理</summary>

- 容器监控
  - CPU/内存使用
  - 容器状态
  - 日志查看
- 应用监控
  - 健康检查
  - 性能指标
  - 告警配置
</details>

### 传统部署

<details>
<summary>详细步骤</summary>

1. 准备环境
2. 构建项目
3. 配置服务器
4. 部署应用
5. 配置反向代理
</details>

## 开发指南

### 项目结构
```
src/
├── frontend/          # Next.js前端项目
│   └── README.md     # 前端开发文档
├── backend/          # Spring Boot后端项目
│   └── README.md    # 后端开发文档
├── docs/            # 项目文档
│   ├── README.md    # 文档索引
│   ├── api/         # API文档
│   ├── design/      # 设计文档
│   └── deploy/      # 部署文档
└── README.md        # 项目说明文档
```

<details>
<summary>frontend/README.md</summary>

```markdown
# 前端开发文档

## 目录结构
- components/: 可复用组件
- pages/: 页面文件
- styles/: 样式文件

## 开发指南
1. 组件开发规范
2. 状态管理方案
3. 样式管理方案
4. 测试规范

## 构建部署
1. 开发环境配置
2. 构建命令说明
3. 部署流程
```
</details>

<details>
<summary>backend/README.md</summary>

```markdown
# 后端开发文档

## 目录结构
- api/: 接口定义
- service/: 业务逻辑
- model/: 数据模型

## 开发指南
1. API设计规范
2. 数据库设计
3. 服务层规范
4. 测试规范

## 部署运维
1. 环境配置
2. 数据库迁移
3. 监控告警
```
</details>

<details>
<summary>docs/README.md</summary>

```markdown
# 项目文档索引

## API文档
- RESTful API说明
- WebSocket接口说明

## 设计文档
- 系统架构设计
- 数据库设计
- 业务流程设计

## 部署文档
- 环境配置说明
- Docker部署指南
- 传统部署指南
```
</details>

### 开发规范

<details>
<summary>代码规范</summary>

- 代码风格
- 命名规范
- 注释规范
- 提交规范
</details>

## 常见问题

<details>
<summary>部署相关</summary>

- Q: Docker容器无法启动?
- A: 检查端口占用和配置文件

[更多常见问题...]
</details>

## 更新日志

### v1.0.0 (ETA: 2025-03-1)
- 🎉 首次发布
- ✨ 基础功能实现
- 🐛 修复已知问题

## 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交更改
4. 发起 Pull Request

## 许可证

[MIT License](LICENSE)