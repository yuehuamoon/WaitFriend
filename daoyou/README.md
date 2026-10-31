#道友请留步

水墨仙侠文字社交 Web游戏。后端为 Spring Boot3.2.5单模块应用，前端为 Vue3 + Vite5。

##本地运行

1.准备 MySQL8.0与 Redis7，默认连接为 `localhost:3306/daoyou`、`localhost:6379`。
2.初始化数据库：

```bash
mysql -uroot -p < src/main/resources/db/schema.sql
```

3.启动后端：

```bash
mvn spring-boot:run
```

4.启动前端：

```bash
cd frontend
yarn install
yarn dev
```

前端地址：`http://localhost:5173`，Knife4j：`http://localhost:8080/doc.html`。

数据库连接可通过 `DB_URL`、`DB_USERNAME`、`DB_PASSWORD`覆盖，Redis可通过 `REDIS_HOST`、`REDIS_PORT`、`REDIS_DATABASE`覆盖。内部修为接口使用 `X-Internal-Token`请求头，默认值由 `INTERNAL_TOKEN`配置。

例如本机 MySQL的 root使用自定义密码时：

```bash
DB_PASSWORD='你的数据库密码' mvn spring-boot:run
```

如果创建了专用数据库账号，推荐同时设置：

```bash
DB_USERNAME=daoyou DB_PASSWORD='你的数据库密码' mvn spring-boot:run
```

管理员账号由 `ADMIN_USERNAMES`指定，默认用户名 `admin`。
