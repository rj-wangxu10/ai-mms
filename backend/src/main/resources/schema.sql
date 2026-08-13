-- 公司预算
CREATE TABLE IF NOT EXISTS company_budget (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    fiscal_year INTEGER NOT NULL,
    total_budget_cny DECIMAL(18,4) NOT NULL DEFAULT 0,
    total_budget_usd DECIMAL(18,4) DEFAULT 0,
    exchange_rate DECIMAL(10,6) DEFAULT 7.100000,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 部门
CREATE TABLE IF NOT EXISTS department (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    company_id INTEGER NOT NULL,
    name VARCHAR(100) NOT NULL,
    monthly_budget_cny DECIMAL(18,4) NOT NULL DEFAULT 0,
    used_budget_cny DECIMAL(18,4) DEFAULT 0,
    manager_id INTEGER,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 用户
CREATE TABLE IF NOT EXISTS sys_user (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    dept_id INTEGER NOT NULL,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    role VARCHAR(20) NOT NULL,
    tool_accounts TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- AI 工具
CREATE TABLE IF NOT EXISTS ai_tool (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(100) NOT NULL,
    billing_mode VARCHAR(20) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    sync_type VARCHAR(20) NOT NULL,
    sync_config TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- AI 模型/套餐
CREATE TABLE IF NOT EXISTS ai_model (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    tool_id INTEGER NOT NULL,
    name VARCHAR(100) NOT NULL,
    unit_price DECIMAL(18,6) NOT NULL DEFAULT 0,
    unit VARCHAR(20) NOT NULL,
    tiered_pricing TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 个人额度记录
CREATE TABLE IF NOT EXISTS quota_record (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    dept_id INTEGER NOT NULL,
    period VARCHAR(7) NOT NULL,
    quota_type VARCHAR(20) NOT NULL,
    total_amount DECIMAL(18,4) NOT NULL DEFAULT 0,
    used_amount DECIMAL(18,4) DEFAULT 0,
    remaining_amount DECIMAL(18,4) NOT NULL DEFAULT 0,
    source_application_id INTEGER,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 消费记录
CREATE TABLE IF NOT EXISTS usage_record (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER,
    dept_id INTEGER NOT NULL,
    tool_id INTEGER NOT NULL,
    model_id INTEGER,
    usage_date DATE NOT NULL,
    usage_quantity DECIMAL(18,4) DEFAULT 0,
    original_amount DECIMAL(18,4) DEFAULT 0,
    original_currency VARCHAR(10),
    amount_cny DECIMAL(18,4) NOT NULL DEFAULT 0,
    source VARCHAR(20) NOT NULL,
    remark VARCHAR(500),
    raw_data TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 额度申请
CREATE TABLE IF NOT EXISTS quota_application (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    applicant_id INTEGER NOT NULL,
    dept_id INTEGER NOT NULL,
    type VARCHAR(20) NOT NULL,
    amount DECIMAL(18,4) NOT NULL,
    reason TEXT,
    status VARCHAR(30) NOT NULL,
    approver_id INTEGER,
    approve_comment TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 预警规则
CREATE TABLE IF NOT EXISTS alert_rule (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    target_type VARCHAR(20) NOT NULL,
    target_id INTEGER NOT NULL,
    threshold_pct INTEGER NOT NULL,
    notify_roles VARCHAR(100),
    enabled BOOLEAN DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 预警日志
CREATE TABLE IF NOT EXISTS alert_log (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    rule_id INTEGER NOT NULL,
    target_type VARCHAR(20) NOT NULL,
    target_id INTEGER NOT NULL,
    actual_pct INTEGER NOT NULL,
    message TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 审计日志
CREATE TABLE IF NOT EXISTS audit_log (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    operator_id INTEGER,
    action VARCHAR(50) NOT NULL,
    target_type VARCHAR(50),
    target_id INTEGER,
    old_value TEXT,
    new_value TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 高频查询索引
CREATE INDEX IF NOT EXISTS idx_quota_user_period ON quota_record(user_id, period);
CREATE INDEX IF NOT EXISTS idx_usage_user_date ON usage_record(user_id, usage_date);
CREATE INDEX IF NOT EXISTS idx_usage_dept_date ON usage_record(dept_id, usage_date);
CREATE INDEX IF NOT EXISTS idx_usage_tool_date ON usage_record(tool_id, usage_date);
CREATE INDEX IF NOT EXISTS idx_application_applicant ON quota_application(applicant_id, status);
CREATE INDEX IF NOT EXISTS idx_application_dept ON quota_application(dept_id, status);
CREATE INDEX IF NOT EXISTS idx_alert_log_target ON alert_log(target_type, target_id, created_at);
