# 前端项目说明

## 技术栈

- Vue 3
- Vite
- Element Plus
- Vue Router
- Axios
- Pinia

## 已完成内容

- Vue 3 + Vite 项目骨架
- 登录页
- 后台主布局
- 路由与登录守卫
- Axios 请求封装
- Pinia 登录态管理
- 商品管理页面
- 采购管理页面
- 销售管理页面
- 库存管理页面
- 溯源查询页面
- 统计分析页面

## 当前目录结构

```text
frontend
├─ public
├─ src
│  ├─ api
│  ├─ assets
│  ├─ components
│  ├─ layout
│  ├─ router
│  ├─ stores
│  ├─ styles
│  ├─ utils
│  └─ views
├─ index.html
├─ package.json
└─ vite.config.js
```

## 启动前要求

当前机器尚未检测到可用的 Node.js 与 npm 环境，因此本次会话中未实际执行依赖安装。你需要先在本机安装：

- Node.js 18 或更高版本
- npm

安装完成后，在 `frontend` 目录执行：

```bash
npm install
npm run dev
```

## 本地开发地址

前端默认地址：

```text
http://localhost:5173
```

后端默认地址：

```text
http://localhost:8080
```

Vite 已配置 `/api` 代理到后端，因此前端开发时无需手动处理跨域。

## 默认登录账号

```text
admin / admin123
```

## 已对接的后端接口

- `/api/auth/*`
- `/api/master/products`
- `/api/master/stocks`
- `/api/master/stocks/low`
- `/api/purchases/*`
- `/api/sales/*`
- `/api/inventory/trace`
- `/api/reports/*`

## 后续建议

- 将采购、销售页面升级为多明细动态表单
- 补充盘点单创建与审核界面
- 增加用户管理、角色管理、日志管理页面
- 增加图表组件展示经营趋势
- 增加表单校验与更细致的交互反馈
