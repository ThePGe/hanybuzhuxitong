# 寒衣补助系统 - 开发 TODO 列表

## 阶段一：项目初始化与基础设施搭建 (Phase 1)
- [x] **项目骨架搭建**：创建 Spring Boot 3 后端工程，初始化 Vue 3 + Vite + Element Plus 前端项目。(已搭建并验证多模块架构，前端已通过 vite 初始化)
- [x] **数据库与 ORM 配置**：执行 `init.sql`，配置 MyBatis-Plus (含分页插件、全局异常处理)。(已配置 `MyBatisPlusConfig`、更新 `User` 实体、并完成 `GlobalExceptionHandler`)
- [x] **权限与安全配置**：集成 Sa-Token，配置多角色鉴权拦截器，实现白名单机制及全局统一鉴权。(已配置 `SaTokenConfig` 拦截器并实现多角色返回 `StpInterfaceImpl`)
- [x] **MinIO 集成**：搭建 MinIO 服务，实现统一的文件上传/下载接口 (用于图片和 Excel 报表)。(已配置 docker-compose 环境和 `MinioUtils`、`FileController` 接口 `/api/file/upload`)
- [x] **接口文档与工具**：集成 Knife4j，引入 Hutool 工具类库。(已在 POM 中引入，添加 `Knife4jConfig` 及相关配置)

## 阶段二：基础业务与登录认证 (Phase 2)
- [x] **统一登录接口**：实现账号密码登录 (学生校验身份证后六位，后台人员校验库中密码)。(已实现 AuthController 的 /auth/login)
- [x] **学生绑定与资格校验**：登录时校验该生是否在“困难认定库”，实现微信扫码绑定/解绑功能。(已实现校验困难库，实现 /student/unbind 微信解绑)
- [x] **白名单控制**：实现“智慧学工”授权登录接口，校验由用户 9447 配置的白名单。(已实现 AuthController 的 /auth/sso/login，完成白名单的拦截)
- [x] **动态路由与菜单**：前端根据登录返回的角色 (学校/学院/辅导员/学生) 动态渲染侧边栏和路由权限。(已实现 LoginVO 中返回 role，配合前端路由机制)

## 阶段三：批次与配置管理 (学校用户) (Phase 3)
- [x] **批次 CRUD**：实现批次的创建及列表查询，支持选择认定学年和困难等级。
- [x] **时间控制机制**：实现申请时间、选款时间的动态修改延长接口。添加全局拦截器判断当前时间是否允许学生操作。
- [x] **服装款式管理**：实现款式图片的 MinIO 上传、尺码推荐表配置、基于性别过滤等增删改查。
- [x] **商品 SKU 管理**：实现自动生成商品编码 (如：男01款XL码) 的规则及对应增删改查。

## 阶段四：学生申请与三级审核流转 (Phase 4)
- [x] **学生申请接口**：结合批次要求，实现新生免填理由、老生必填理由的动态校验。提交申请记录。
- [x] **辅导员初审模块**：
  - 本班申请列表展示与分页 (10/20/50条)。
  - 批量通过/拒绝 (可填拒绝理由)。
  - 提交至学院接口 (提交后不可修改；若申请时间截止，系统默认未审数据全部通过)。
- [x] **学院复审模块**：
  - 学院级待审列表展示。
  - 批量审核及提交至学校接口 (时间截止后同样适用默认通过机制)。
- [x] **学校终审模块**：
  - 学校级待审列表及终审。
  - 终审结束后触发通知结果推送逻辑。
  - **完成说明**: 已实现 SubsidyApply 实体、Mapper、Service 及其实现类；已实现 StudentController.apply 接口并添加了老生新生动态校验逻辑；已实现 AuditController 包含列表展示 (getAuditList)、批量审核 (approve)、提交至下一级 (submitToNext) 以及终审结果通知 (notifyStudents)，并已编写相关单元测试且通过。

## 阶段五：衣服选款与尺码登记 (Phase 5)
- [x] **学生选款接口**：仅通过审核的学生可进入，前端根据学生性别强制过滤展示对应的款式和尺码。
- [x] **学生修改尺码**：选款截止时间前允许学生自行修改。
- [x] **管理员干预接口**：选款截止时间后，学校用户拥有单独修改个别学生款式和尺码的权限。
  - **完成说明**: 已实现 `SelectionService` 及相关 `DTO`，在 `StudentController` 新增 `/api/student/selection/clothing/list` 和 `/api/student/selection` 获取款式并带出 SKU 及提交选款。在 `ClothingController` 新增 `/api/school/clothing/selection/update` 提供给管理员进行强制干预修改，并已完成单元测试。

## 阶段六：数据统计与 Excel 导出 (Phase 6)
- [✅] **实时统计大屏/接口**：实时统计各款式、男女、尺码的选择人数。（已完成 `GET /api/statistics/realtime`）
- [✅] **学院维度发放统计**：按学院维度聚合统计 SKU 需求，方便物资发放。（已完成 `GET /api/statistics/college`）
- [✅] **Hutool Excel 导出**：
  - 导出申请状态表：包含所有申请学生的审核进度。
  - 导出汇总结果表：包含最终审核结果及所选服装。
  - 导出的 Excel 保存至 MinIO 并提供前端下载链接。（已完成 `GET /api/statistics/export?type=application/summary`）

## 阶段七：系统联调与测试 (Phase 7)
- [✅] 前后端全链路接口联调。
- [ ] 时间控制边界测试 (验证申请期前、期间、结束后各类按钮和接口的拦截)。
- [ ] 角色越权与权限隔离测试 (防平权或越权访问)。

