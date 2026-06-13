# API 测试指南

本文档用于记录并维护系统中已开发的接口测试说明。

## 1. 测试接口 (TestController)

### 1.1 测试连通性
- **所属 Controller**：`TestController`
- **请求方式**：`GET`
- **请求 URL**：`/api/test/hello`
- **鉴权要求**：无需 Token (或当前临时放行)
- **响应说明**：
  ```json
  {
    "code": 200,
    "msg": "操作成功",
    "data": "Hello World!"
  }
  ```

## 2. 登录与认证模块 (AuthController & StudentController)

### 2.1 统一账号登录
- **所属 Controller**：`AuthController`
- **请求方式**：`POST`
- **请求 URL**：`/api/auth/login`
- **鉴权要求**：无需 Token
- **请求体 (JSON)**：
  ```json
  {
    "username": "2020001",
    "password": "101234"
  }
  ```
- **响应说明**：
  ```json
  {
    "code": 200,
    "msg": "操作成功",
    "data": {
      "token": "sa-token-...",
      "role": "student",
      "userInfo": {
        "id": 5,
        "name": "李新生(男)",
        "username": "2020001",
        "role": "student",
        "gender": "M",
        "isNewStudent": true,
        "auditStatus": null
      }
    }
  }
  ```

### 2.2 智慧学工授权登录 (白名单)
- **所属 Controller**：`AuthController`
- **请求方式**：`POST`
- **请求 URL**：`/api/auth/sso/login`
- **鉴权要求**：无需 Token
- **请求体 (JSON)**：
  ```json
  {
    "username": "school01",
    "password": "123456"
  }
  ```
- **响应说明**：
  同 3.1。

### 2.3 获取当前用户信息
- **所属 Controller**：`AuthController`
- **请求方式**：`GET`
- **请求 URL**：`/api/auth/userInfo`
- **鉴权要求**：需 Token (`Authorization: Bearer <token>`)
- **响应说明**：
  ```json
  {
    "code": 200,
    "msg": "操作成功",
    "data": {
      "id": 5,
      "name": "李新生(男)",
      "username": "2020001",
      "role": "student",
      "gender": "M",
      "isNewStudent": true,
      "auditStatus": null
    }
  }
  ```

### 2.4 微信解绑 (学生端)
- **所属 Controller**：`StudentController`
- **请求方式**：`POST`
- **请求 URL**：`/api/student/unbind`
- **鉴权要求**：需 Token 且必须是 student 角色
- **请求体 (JSON)**：
  ```json
  {
    "studentId": "2020001"
  }
  ```
- **响应说明**：
  ```json
  {
    "code": 200,
    "msg": "操作成功",
    "data": true
  }
  ```

## 3. 文件存储模块 (FileController)

### 3.1 文件/图片上传
- **所属 Controller**：`FileController`
- **请求方式**：`POST`
- **请求 URL**：`/api/file/upload`
- **鉴权要求**：需配置临时放行或提供 Token
- **请求体 (FormData)**：
  - `file`: (文件对象)
- **响应说明**：
  ```json
  {
    "code": 200,
    "msg": "操作成功",
    "data": {
      "url": "http://localhost:9000/winter-clothing/abcd1234.jpg"
    }
  }
  ```

## 4. 数据统计与导出模块 (StatisticsController)

### 4.1 实时选款统计
- **所属 Controller**：`StatisticsController`
- **请求方式**：`GET`
- **请求 URL**：`/api/statistics/realtime`
- **鉴权要求**：需 Token 且必须是 school 角色
- **响应说明**：
  ```json
  {
    "code": 200,
    "msg": "操作成功",
    "data": [
      {
        "clothingName": "波司登极寒系列羽绒服(男)",
        "size": "L",
        "count": 10
      }
    ]
  }
  ```

### 4.2 学院维度发放需求统计
- **所属 Controller**：`StatisticsController`
- **请求方式**：`GET`
- **请求 URL**：`/api/statistics/college`
- **鉴权要求**：需 Token 且必须是 school 角色
- **响应说明**：
  ```json
  {
    "code": 200,
    "msg": "操作成功",
    "data": [
      {
        "studentId": "2020001",
        "name": "李雷",
        "gender": "M",
        "college": "计算机学院",
        "clothingId": 1,
        "clothingName": "波司登极寒系列羽绒服(男)",
        "skuId": 2,
        "size": "L",
        "count": 1
      }
    ]
  }
  ```

### 4.3 导出 Excel 报表
- **所属 Controller**：`StatisticsController`
- **请求方式**：`GET`
- **请求 URL**：`/api/statistics/export?type=application` (可选值: application / summary)
- **鉴权要求**：需 Token 且必须是 school 角色
- **响应说明**：
  ```json
  {
    "code": 200,
    "msg": "操作成功",
    "data": {
      "downloadUrl": "http://localhost:9000/winter-clothing/abcd1234.xlsx"
    }
  }
  ```