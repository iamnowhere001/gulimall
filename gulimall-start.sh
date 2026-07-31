#!/usr/bin/env bash
#
# gulimall 一键启动脚本（macOS / Linux）
# ------------------------------------------------------------
# 启动内容：
#   1. 中间件（docker-compose）：MySQL / Redis / Nacos / ES / Kibana / RabbitMQ / Nginx
#   2. 后端微服务（11 个 gulimall-* 模块）+ 后台管理 renren-fast
#   3. 前端后台管理（renren-fast-vue，npm run dev）
#
# 前台商城通过 Nginx（80）→ Gateway（88）→ 各微服务访问，
# 需在 /etc/hosts 中配置 *.gulimall.com → 127.0.0.1（脚本不会自动修改 hosts）。
#
# 用法：
#   ./gulimall-start.sh start      启动全部（默认）
#   ./gulimall-start.sh stop       停止全部
#   ./gulimall-start.sh restart    重启全部
#   ./gulimall-start.sh status     查看运行状态
#   ./gulimall-start.sh backend    仅启动后端（中间件 + 微服务，不启前端）
#
# 环境变量（可选）：
#   SKIP_DOCKER=1     跳过 docker-compose 中间件启动（中间件已手动启动时用）
#   SKIP_BUILD=1      跳过 maven 打包，直接运行已有 jar（加快二次启动）
#   SKIP_FRONTEND=1   不启动 renren-fast-vue 前端
#
# 配置说明：各微服务配置已收敛到各自的 src/main/resources/application.yml（单一文件），
# Nacos 现在仅作为「服务注册与发现」使用（网关 lb:// 路由与 OpenFeign 依赖），
# 不再依赖 Nacos 配置中心 / 各 namespace 远程配置，启动前无需导入任何远程配置。
#
# 注意：本项目基于 Spring Boot 2.2.5 + Lombok + Redisson 3.12，必须使用 JDK 8 编译运行。
#       脚本会自动通过 /usr/libexec/java_home -v 1.8 定位 JDK 8（macOS）。
#       若系统默认 java 为 JDK 9+，直接 java -jar 启动会导致反射/模块化错误。
#

set -euo pipefail

# ===================== 基础配置 =====================
# 强制使用 JDK 8（项目基于 Spring Boot 2.x + Redisson 3.12，不兼容 JDK 9+）
export JAVA_HOME="$(/usr/libexec/java_home -v 1.8 2>/dev/null || echo "$JAVA_HOME")"
if [[ -z "$JAVA_HOME" || ! -x "$JAVA_HOME/bin/java" ]]; then
  echo "ERROR: 未找到 JDK 8，请先安装" >&2
  exit 1
fi
export PATH="$JAVA_HOME/bin:$PATH"

# 脚本所在目录即项目根目录
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_DIR"

LOG_DIR="$PROJECT_DIR/logs"
PID_DIR="$PROJECT_DIR/.pids"
mkdir -p "$LOG_DIR" "$PID_DIR"

# 微服务模块列表（目录名 -> 日志名）
SERVICES=(
  "gulimall-gateway"
  "gulimall-auth-server"
  "gulimall-product"
  "gulimall-coupon"
  "gulimall-member"
  "gulimall-order"
  "gulimall-ware"
  "gulimall-cart"
  "gulimall-search"
  "gulimall-seckill"
  "gulimall-third-party"
)

# 后台管理 jar（位于项目根目录）
RENREN_FAST_JAR="$PROJECT_DIR/renren-fast.jar"
# 前端工程目录
FRONTEND_DIR="$PROJECT_DIR/renren-fast-vue"

# ===================== 颜色输出 =====================
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; BLUE='\033[0;34m'; NC='\033[0m'
log()  { echo -e "${GREEN}[$(date +'%H:%M:%S')]${NC} $*"; }
warn() { echo -e "${YELLOW}[$(date +'%H:%M:%S')] WARN:${NC} $*"; }
err()  { echo -e "${RED}[$(date +'%H:%M:%S')] ERROR:${NC} $*"; }
info() { echo -e "${BLUE}[$(date +'%H:%M:%S')]${NC} $*"; }

# ===================== 工具函数 =====================
save_pid() { echo "$1" > "$PID_DIR/$2.pid"; }
read_pid() { [[ -f "$PID_DIR/$1.pid" ]] && cat "$PID_DIR/$1.pid" || echo ""; }

is_running() {
  local pid; pid="$(read_pid "$1")"
  [[ -n "$pid" ]] && kill -0 "$pid" 2>/dev/null
}

wait_for_port() {
  local host="$1" port="$2" svc="$3" tries=60
  for ((i=0; i<tries; i++)); do
    if (echo > "/dev/tcp/$host/$port") 2>/dev/null; then
      log "$svc 端口 $port 已就绪"
      return 0
    fi
    sleep 2
  done
  warn "$svc 端口 $port 在等待期内未就绪（服务可能仍在启动，请稍后查看日志）"
}

# ===================== 前台域名解析检查 =====================
# 前台商城页面与接口均硬编码 *.gulimall.com，域名解析不到 127.0.0.1 时
# 即使所有服务启动正常，页面也会无法访问。macOS 无 getent，使用 dscacheutil 兼容。
resolve_host() {
  local d="$1" ip=""
  if command -v getent >/dev/null 2>&1; then
    ip="$(getent hosts "$d" 2>/dev/null | awk '{print $1}' | head -n1)"
  fi
  if [[ -z "$ip" ]] && command -v dscacheutil >/dev/null 2>&1; then
    ip="$(dscacheutil -q host -a name "$d" 2>/dev/null | awk -F': ' '/^ip_address:/{print $2; exit}')"
  fi
  echo "$ip"
}

check_hosts() {
  local missing=()
  local domains=(gulimall.com search.gulimall.com item.gulimall.com auth.gulimall.com
                 cart.gulimall.com order.gulimall.com member.gulimall.com seckill.gulimall.com)
  for d in "${domains[@]}"; do
    ip="$(resolve_host "$d")"
    [[ "$ip" != "127.0.0.1" ]] && missing+=("$d")
  done
  if [[ ${#missing[@]} -gt 0 ]]; then
    warn "以下前台域名未解析到 127.0.0.1，页面将无法正常访问："
    for d in "${missing[@]}"; do echo -e "    ${YELLOW}$d${NC}"; done
    echo -e "  请先执行：${GREEN}sudo ./setup-hosts.sh${NC}"
  else
    log "前台商城域名解析正常（*.gulimall.com -> 127.0.0.1）"
  fi
}

# ===================== 中间件 =====================
start_middleware() {
  if [[ "${SKIP_DOCKER:-0}" == "1" ]]; then
    warn "已设置 SKIP_DOCKER=1，跳过 docker-compose 中间件启动"
    return
  fi
  if ! command -v docker >/dev/null 2>&1; then
    err "未检测到 docker，无法启动中间件。可手动启动中间件后使用 SKIP_DOCKER=1 运行本脚本。"
    return 1
  fi
  if ! docker compose version >/dev/null 2>&1 && ! docker-compose version >/dev/null 2>&1; then
    err "未检测到 docker compose / docker-compose 命令"
    return 1
  fi
  local dc="docker compose"; docker compose version >/dev/null 2>&1 || dc="docker-compose"
  log "启动中间件（docker-compose）..."
  $dc up -d
  sleep 5
  wait_for_port localhost 8848 nacos
  wait_for_port localhost 3306 mysql
  wait_for_port localhost 6379 redis
  wait_for_port localhost 9200 elasticsearch
  wait_for_port localhost 5672 rabbitmq
}

stop_middleware() {
  if [[ "${SKIP_DOCKER:-0}" == "1" ]]; then return; fi
  local dc="docker compose"; docker compose version >/dev/null 2>&1 || dc="docker-compose"
  if $dc ps -q >/dev/null 2>&1; then
    log "停止中间件（docker-compose down）..."
    $dc down || true
  fi
}

# ===================== 后端微服务 =====================
build_backend() {
  if [[ "${SKIP_BUILD:-0}" == "1" ]]; then
    warn "已设置 SKIP_BUILD=1，跳过 maven 打包，直接运行已有 jar"
    return
  fi
  log "使用 maven 打包全部后端模块（跳过测试，请耐心等待）..."
  mvn -q -DskipTests clean package
}

start_service() {
  local module="$1"
  if is_running "$module"; then
    warn "$module 已在运行 (PID $(read_pid "$module"))，跳过"
    return
  fi
  local jar
  jar=$(ls "$PROJECT_DIR/$module"/target/gulimall-*.jar 2>/dev/null | head -n1 || true)
  if [[ -z "$jar" || ! -f "$jar" ]]; then
    err "$module 未找到可运行 jar，请先执行构建（去掉 SKIP_BUILD）"
    return 1
  fi
  log "启动 $module ..."
  nohup "$JAVA_HOME/bin/java" -jar "$jar" > "$LOG_DIR/$module.log" 2>&1 &
  save_pid "$!" "$module"
}

start_backend() {
  start_middleware
  build_backend
  log "依次启动 ${#SERVICES[@]} 个微服务模块..."
  for s in "${SERVICES[@]}"; do
    start_service "$s" || true
  done

  # 后台管理 renren-fast
  if [[ -f "$RENREN_FAST_JAR" ]]; then
    if is_running renren-fast; then
      warn "renren-fast 已在运行，跳过"
    else
      log "启动 后台管理 renren-fast ..."
      nohup "$JAVA_HOME/bin/java" -jar "$RENREN_FAST_JAR" > "$LOG_DIR/renren-fast.log" 2>&1 &
      save_pid "$!" renren-fast
    fi
  else
    warn "未找到 $RENREN_FAST_JAR，跳过后台管理（如需可用 renren-fast-tmp 模块手动启动）"
  fi
  log "后端启动完成。日志目录：$LOG_DIR"
}

stop_backend() {
  for s in "${SERVICES[@]}" renren-fast; do
    if is_running "$s"; then
      log "停止 $s (PID $(read_pid "$s"))..."
      kill "$(read_pid "$s")" 2>/dev/null || true
      rm -f "$PID_DIR/$s.pid"
    fi
  done
}

# ===================== 前端 =====================
start_frontend() {
  if [[ "${SKIP_FRONTEND:-0}" == "1" ]]; then
    warn "已设置 SKIP_FRONTEND=1，跳过前端启动"
    return
  fi
  if [[ ! -d "$FRONTEND_DIR" ]]; then
    err "未找到前端目录 $FRONTEND_DIR"
    return 1
  fi
  if is_running renren-fast-vue; then
    warn "renren-fast-vue 已在运行，跳过"
    return
  fi
  if ! command -v node >/dev/null 2>&1; then
    err "未检测到 node，无法启动前端"
    return 1
  fi
  log "准备前端依赖（npm install，首次较慢）..."
  ( cd "$FRONTEND_DIR" && npm install ) || warn "npm install 失败，尝试直接启动"
  log "启动 前端 renren-fast-vue (npm run dev) ..."
  nohup bash -c "cd '$FRONTEND_DIR' && npm run dev" > "$LOG_DIR/renren-fast-vue.log" 2>&1 &
  save_pid "$!" renren-fast-vue
  log "前端启动中，默认访问 http://localhost:8001 （详见前端日志）"
}

stop_frontend() {
  if is_running renren-fast-vue; then
    log "停止 renren-fast-vue (PID $(read_pid renren-fast-vue))..."
    kill "$(read_pid renren-fast-vue)" 2>/dev/null || true
    rm -f "$PID_DIR/renren-fast-vue.pid"
  fi
}

# ===================== 状态 =====================
status_all() {
  info "=== 服务运行状态 ==="
  for s in "${SERVICES[@]}" renren-fast renren-fast-vue; do
    if is_running "$s"; then
      echo -e "  ${GREEN}RUNNING${NC}  $s (PID $(read_pid "$s"))"
    else
      echo -e "  ${RED}STOPPED${NC}  $s"
    fi
  done
  if [[ "${SKIP_DOCKER:-0}" != "1" ]]; then
    local dc="docker compose"; docker compose version >/dev/null 2>&1 || dc="docker-compose"
    echo; info "=== 中间件（docker-compose）==="
    $dc ps 2>/dev/null || true
  fi
}

# ===================== 主流程 =====================
case "${1:-start}" in
  start)
    check_hosts
    start_backend
    start_frontend
    echo
    log "全部启动完成 ✅"
    echo
    info "==================== 访问地址 ===================="
    echo -e "  ${GREEN}【前台商城】${NC}（hosts 需配置 *.gulimall.com → 127.0.0.1）"
    echo -e "    首页        http://gulimall.com"
    echo -e "    商品检索    http://search.gulimall.com/list.html"
    echo -e "    商品详情    http://item.gulimall.com/{skuId}.html"
    echo -e "    登录        http://auth.gulimall.com/login.html"
    echo -e "    注册        http://auth.gulimall.com/reg.html"
    echo -e "    购物车      http://cart.gulimall.com/cartList.html"
    echo -e "    确认订单    http://order.gulimall.com/confirm.html"
    echo -e "    会员订单    http://member.gulimall.com/memberOrder.html"
    echo -e "    秒杀        http://seckill.gulimall.com"
    echo
    echo -e "  ${GREEN}【后台管理】${NC}"
    echo -e "    管理前端    http://localhost:8001"
    echo -e "    接口网关    http://localhost:88"
    echo
    echo -e "  ${GREEN}【中间件控制台】${NC}"
    echo -e "    Nacos       http://localhost:8848/nacos"
    echo -e "    RabbitMQ    http://localhost:15672  (guest/guest)"
    echo -e "    Kibana      http://localhost:5601"
    echo -e "    Elasticsearch http://localhost:9200"
    echo -e "================================================="
    ;;
  backend)
    start_backend
    log "后端启动完成 ✅（不含前端）"
    ;;
  stop)
    stop_frontend
    stop_backend
    stop_middleware
    log "已全部停止 ✅"
    ;;
  restart)
    stop_frontend; stop_backend; stop_middleware
    sleep 2
    "$0" start
    ;;
  status)
    status_all
    ;;
  *)
    err "未知参数: $1"
    echo "用法: $0 {start|backend|stop|restart|status}"
    exit 1
    ;;
esac
