#!/bin/bash
set -e

#==============================================================
# AI 费用管理平台 — Ubuntu 一键部署脚本
# 用法: chmod +x deploy.sh && sudo ./deploy.sh
#==============================================================

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

echo -e "${CYAN}========================================${NC}"
echo -e "${CYAN}  AI 费用管理平台 — 一键部署${NC}"
echo -e "${CYAN}========================================${NC}"

# 检查是否 root
if [ "$EUID" -ne 0 ]; then
    echo -e "${YELLOW}[INFO] 建议使用 root 用户运行，当前非 root，尝试 sudo...${NC}"
    exec sudo "$0" "$@"
fi

# 配置
INSTALL_DIR="/opt/ai-mms"
JAVA_VERSION=21
NODE_VERSION=20
APP_USER="aimms"
APP_PORT=8080
WEB_PORT=80

echo -e "${YELLOW}[1/8] 安装系统依赖...${NC}"
apt-get update -qq
apt-get install -y -qq curl wget git unzip tar build-essential > /dev/null 2>&1

echo -e "${YELLOW}[2/8] 安装 JDK ${JAVA_VERSION}...${NC}"
if ! command -v java &> /dev/null; then
    apt-get install -y -qq openjdk-${JAVA_VERSION}-jdk-headless > /dev/null 2>&1
fi
JAVA_INSTALLED=$(java -version 2>&1 | head -1 | awk -F '"' '{print $2}' | cut -d'.' -f1)
if [ "$JAVA_INSTALLED" != "$JAVA_VERSION" ] && [ "$JAVA_INSTALLED" != "$((JAVA_VERSION-1))" ]; then
    echo -e "${YELLOW}当前 Java 版本: ${JAVA_INSTALLED}, 安装 OpenJDK ${JAVA_VERSION}...${NC}"
    apt-get install -y -qq openjdk-${JAVA_VERSION}-jdk-headless > /dev/null 2>&1
fi
echo -e "${GREEN}  Java: $(java -version 2>&1 | head -1)${NC}"

echo -e "${YELLOW}[3/8] 安装 Node.js ${NODE_VERSION}...${NC}"
if ! command -v node &> /dev/null; then
    curl -fsSL "https://deb.nodesource.com/setup_${NODE_VERSION}.x" | bash - > /dev/null 2>&1
    apt-get install -y -qq nodejs > /dev/null 2>&1
fi
echo -e "${GREEN}  Node: $(node -v)${NC}"
echo -e "${GREEN}  npm:  $(npm -v)${NC}"

echo -e "${YELLOW}[4/8] 安装 Nginx...${NC}"
if ! command -v nginx &> /dev/null; then
    apt-get install -y -qq nginx > /dev/null 2>&1
fi
echo -e "${GREEN}  Nginx: $(nginx -v 2>&1)${NC}"

echo -e "${YELLOW}[5/8] 创建应用用户和目录...${NC}"
if ! id -u "$APP_USER" &> /dev/null; then
    useradd -r -m -d "$INSTALL_DIR" -s /bin/bash "$APP_USER"
fi
mkdir -p "$INSTALL_DIR"/{backend,frontend,data,logs}
chown -R "$APP_USER":"$APP_USER" "$INSTALL_DIR"

echo -e "${YELLOW}[6/8] 复制项目文件...${NC}"
# 获取脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# 复制后端
if [ -d "$SCRIPT_DIR/backend" ]; then
    cp -r "$SCRIPT_DIR/backend/src" "$INSTALL_DIR/backend/"
    cp "$SCRIPT_DIR/backend/pom.xml" "$INSTALL_DIR/backend/"
    echo -e "${GREEN}  后端源码已复制${NC}"
else
    echo -e "${RED}  错误: 未找到 backend 目录!${NC}"
    exit 1
fi

# 复制前端
if [ -d "$SCRIPT_DIR/frontend" ]; then
    cp -r "$SCRIPT_DIR/frontend/src" "$INSTALL_DIR/frontend/"
    cp "$SCRIPT_DIR/frontend/index.html" "$INSTALL_DIR/frontend/"
    cp "$SCRIPT_DIR/frontend/package.json" "$INSTALL_DIR/frontend/"
    cp "$SCRIPT_DIR/frontend/tsconfig.json" "$INSTALL_DIR/frontend/"
    cp "$SCRIPT_DIR/frontend/tsconfig.node.json" "$INSTALL_DIR/frontend/"
    cp "$SCRIPT_DIR/frontend/vite.config.ts" "$INSTALL_DIR/frontend/"
    echo -e "${GREEN}  前端源码已复制${NC}"
else
    echo -e "${RED}  错误: 未找到 frontend 目录!${NC}"
    exit 1
fi

chown -R "$APP_USER":"$APP_USER" "$INSTALL_DIR"

echo -e "${YELLOW}[7/8] 构建后端 JAR...${NC}"
cd "$INSTALL_DIR/backend"
# 安装 Maven
if ! command -v mvn &> /dev/null; then
    echo "  安装 Maven..."
    MAVEN_VERSION=3.9.9
    wget -q "https://dlcdn.apache.org/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz" -O /tmp/maven.tar.gz
    tar -xzf /tmp/maven.tar.gz -C /opt/
    ln -sf /opt/apache-maven-${MAVEN_VERSION}/bin/mvn /usr/local/bin/mvn
    rm -f /tmp/maven.tar.gz
fi
echo -e "${GREEN}  Maven: $(mvn -v 2>&1 | head -1)${NC}"

# 构建
su - "$APP_USER" -c "cd $INSTALL_DIR/backend && mvn clean package -DskipTests -q" 2>&1 | tail -5
JAR_FILE=$(ls "$INSTALL_DIR/backend/target/ai-mms-*.jar" 2>/dev/null | head -1)
if [ -z "$JAR_FILE" ]; then
    echo -e "${RED}  后端构建失败!${NC}"
    exit 1
fi
echo -e "${GREEN}  JAR: $(basename $JAR_FILE)${NC}"

echo -e "${YELLOW}[8/8] 构建前端...${NC}"
cd "$INSTALL_DIR/frontend"
su - "$APP_USER" -c "cd $INSTALL_DIR/frontend && npm install --silent 2>&1" | tail -3
su - "$APP_USER" -c "cd $INSTALL_DIR/frontend && npx vite build 2>&1" | tail -5
if [ ! -d "$INSTALL_DIR/frontend/dist" ]; then
    echo -e "${RED}  前端构建失败!${NC}"
    exit 1
fi
echo -e "${GREEN}  前端 dist/ 已生成${NC}"

#==============================================================
# 配置 Nginx
#==============================================================
echo -e "${CYAN}--- 配置 Nginx ---${NC}"
cat > /etc/nginx/sites-available/ai-mms << 'NGINX_EOF'
server {
    listen 80;
    server_name _;

    # 前端静态资源
    root /opt/ai-mms/frontend/dist;
    index index.html;

    # Vue Router history 模式
    location / {
        try_files $uri $uri/ /index.html;
    }

    # API 反向代理到后端
    location /api/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_read_timeout 60s;
    }

    # 静态资源缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff2?)$ {
        expires 30d;
        add_header Cache-Control "public, immutable";
    }
}
NGINX_EOF

ln -sf /etc/nginx/sites-available/ai-mms /etc/nginx/sites-enabled/ai-mms
rm -f /etc/nginx/sites-enabled/default
nginx -t 2>&1
systemctl restart nginx
systemctl enable nginx > /dev/null 2>&1
echo -e "${GREEN}  Nginx 配置完成${NC}"

#==============================================================
# 配置 Systemd 服务
#==============================================================
echo -e "${CYAN}--- 配置 Systemd 服务 ---${NC}"
cat > /etc/systemd/system/ai-mms.service << 'SYSTEMD_EOF'
[Unit]
Description=AI 费用管理平台后端服务
After=network.target

[Service]
Type=simple
User=aimms
Group=aimms
WorkingDirectory=/opt/ai-mms/backend
ExecStart=/usr/bin/java -Xms256m -Xmx512m -jar /opt/ai-mms/backend/target/ai-mms-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod --SQLITE_PATH=/opt/ai-mms/data/ai-mms.db
ExecStop=/bin/kill -TERM $MAINPID
Restart=on-failure
RestartSec=10
StandardOutput=append:/opt/ai-mms/logs/app.log
StandardError=append:/opt/ai-mms/logs/error.log

[Install]
WantedBy=multi-user.target
SYSTEMD_EOF

systemctl daemon-reload
systemctl enable ai-mms > /dev/null 2>&1
systemctl restart ai-mms
echo -e "${GREEN}  ai-mms 服务已启动${NC}"

#==============================================================
# 等待后端启动
#==============================================================
echo -e "${YELLOW}等待后端启动...${NC}"
for i in $(seq 1 30); do
    if curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/v1/dashboard/admin?period=2026-08 | grep -q "200"; then
        echo -e "${GREEN}  后端已就绪!${NC}"
        break
    fi
    if [ $i -eq 30 ]; then
        echo -e "${RED}  后端启动超时, 请检查日志: tail -f /opt/ai-mms/logs/app.log${NC}"
    fi
    sleep 1
done

#==============================================================
# 完成
#==============================================================
SERVER_IP=$(hostname -I | awk '{print $1}')
echo ""
echo -e "${CYAN}========================================${NC}"
echo -e "${GREEN}  部署完成!${NC}"
echo -e "${CYAN}========================================${NC}"
echo ""
echo -e "  访问地址:  ${GREEN}http://${SERVER_IP}${NC}"
echo -e "  后端端口:  ${APP_PORT}"
echo -e "  Web端口:   ${WEB_PORT}"
echo ""
echo -e "  ${YELLOW}常用命令:${NC}"
echo -e "    查看后端状态:  systemctl status ai-mms"
echo -e "    重启后端:      systemctl restart ai-mms"
echo -e "    查看后端日志:  journalctl -u ai-mms -f"
echo -e "    查看应用日志:  tail -f /opt/ai-mms/logs/app.log"
echo -e "    重启 Nginx:    systemctl restart nginx"
echo -e "    查看 Nginx日志: tail -f /var/log/nginx/access.log"
echo ""
echo -e "  ${YELLOW}数据目录:  /opt/ai-mms/data/ai-mms.db${NC}"
echo -e "  ${YELLOW}安装目录:  /opt/ai-mms${NC}"
echo ""
