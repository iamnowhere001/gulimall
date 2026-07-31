#!/usr/bin/env bash
#
# 一键把 gulimall 前台商城所需的 8 个域名解析到 127.0.0.1。
# 解决“服务都起来了但 *.gulimall.com 页面打不开”的问题。
#
# 说明：
#   /etc/hosts 不支持通配符（*.gulimall.com 不生效），必须逐条枚举。
#   修改后 macOS 需刷新 DNS 缓存（mDNSResponder）才会生效。
#   写 /etc/hosts 需要 root 权限，故本脚本内部用 sudo。
#
# 用法：
#   sudo ./setup-hosts.sh        写入缺失的域名 + 刷新 DNS 缓存
#   ./setup-hosts.sh check        仅检查当前解析情况（无需 sudo）

set -euo pipefail

HOSTS_FILE="/etc/hosts"
DOMAINS=(
  "gulimall.com"
  "search.gulimall.com"
  "item.gulimall.com"
  "auth.gulimall.com"
  "cart.gulimall.com"
  "order.gulimall.com"
  "member.gulimall.com"
  "seckill.gulimall.com"
)

if [[ "${1:-}" == "check" ]]; then
  echo "=== 当前域名解析检查 ==="
  for d in "${DOMAINS[@]}"; do
    ip=$(getent hosts "$d" 2>/dev/null | awk '{print $1}' | head -n1 || true)
    if [[ "$ip" == "127.0.0.1" ]]; then
      echo "  OK    $d -> 127.0.0.1"
    elif [[ -n "$ip" ]]; then
      echo "  WARN  $d -> $ip  (不是 127.0.0.1，可能被其他配置占用)"
    else
      echo "  MISS  $d  (未解析)"
    fi
  done
  exit 0
fi

echo "需要将以下域名写入 $HOSTS_FILE（需要 sudo 权限）："
printf '  %s\n' "${DOMAINS[@]}"

# 用 sudo 写入：仅在条目缺失时追加，幂等
sudo bash -c '
hosts_file="'"$HOSTS_FILE"'"
domains=('"${DOMAINS[*]}"')
added=0
for d in "${domains[@]}"; do
  if ! grep -qE "[[:space:]]${d}([[:space:]]|$)" "$hosts_file"; then
    echo "127.0.0.1 $d" >> "$hosts_file"
    added=$((added+1))
  fi
done
if [[ $added -eq 0 ]]; then
  echo "所有域名已存在，无需修改。"
else
  echo "已追加 $added 条记录。"
fi
'

echo "刷新 macOS DNS 缓存..."
sudo dscacheutil -flushcache 2>/dev/null || true
sudo killall -HUP mDNSResponder 2>/dev/null || true

echo "完成 ✅ 现在可用 gulimall.com 等域名访问前台商城。"
echo "验证："
echo "  getent hosts gulimall.com"
echo "  curl -sI http://gulimall.com/ | head -n1"
