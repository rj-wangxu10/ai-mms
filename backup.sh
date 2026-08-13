#!/usr/bin/env bash
# AI 费用管理平台 — 数据库备份脚本
# 用法: ./backup.sh [db_path]
# 建议配合 crontab: 0 2 * * * /path/to/backup.sh

DB_PATH=${1:-./data/ai-mms.db}
BACKUP_DIR="./backups"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)

mkdir -p "$BACKUP_DIR"

if [ ! -f "$DB_PATH" ]; then
  echo "[ERR] 数据库文件不存在: $DB_PATH"
  exit 1
fi

BACKUP_FILE="$BACKUP_DIR/ai-mms_${TIMESTAMP}.db"

# SQLite 在线备份 (使用 .backup 命令，保证一致性)
sqlite3 "$DB_PATH" ".backup '$BACKUP_FILE'" 2>/dev/null || cp "$DB_PATH" "$BACKUP_FILE"

# 压缩
gzip -f "$BACKUP_FILE"

# 保留最近 30 天的备份
find "$BACKUP_DIR" -name "ai-mms_*.db.gz" -mtime +30 -delete

echo "[OK] 备份完成: ${BACKUP_FILE}.gz"
