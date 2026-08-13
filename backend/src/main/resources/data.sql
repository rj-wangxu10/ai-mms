-- ================================================================
-- 演示数据 — 3个月 (2026-06, 2026-07, 2026-08)
-- 每个用户都有不同的消费模式，切换用户时数据明显不同
-- ================================================================

-- ===== 公司年度预算 =====
INSERT OR IGNORE INTO company_budget (id, fiscal_year, total_budget_cny, total_budget_usd, exchange_rate) VALUES
(1, 2026, 1000000.0000, 140845.0700, 7.100000);

-- ===== 部门 =====
INSERT OR IGNORE INTO department (id, company_id, name, monthly_budget_cny, used_budget_cny, manager_id) VALUES
(1, 1, '研发部', 100000.0000, 38500.0000, 2),
(2, 1, '产品部', 50000.0000, 12800.0000, 3);

-- ===== 用户 =====
INSERT OR IGNORE INTO sys_user (id, dept_id, username, email, role, tool_accounts) VALUES
(1, 1, 'admin', 'admin@aimms.local', 'admin', '{}'),
(2, 1, 'manager_rd', 'manager.rd@aimms.local', 'manager', '{"github":"mgr_rd","claude":"mgr_rd_claude"}'),
(3, 2, 'manager_pm', 'manager.pm@aimms.local', 'manager', '{"feishu":"mgr_pm","copilot":"mgr_pm_gh"}'),
(4, 1, 'employee01', 'emp01@aimms.local', 'employee', '{"github":"emp01","claude":"emp01_claude","codex":"emp01_codex"}'),
(5, 1, 'employee02', 'emp02@aimms.local', 'employee', '{"github":"emp02","workbuddy":"emp02_wb","uniapi":"emp02_api"}'),
(6, 2, 'employee03', 'emp03@aimms.local', 'employee', '{"feishu":"emp03","fastgpt":"emp03_gpt","copilot":"emp03_gh"}');

-- ===== AI 工具 =====
INSERT OR IGNORE INTO ai_tool (id, name, billing_mode, currency, sync_type, sync_config) VALUES
(1, 'GitHub Copilot', 'seat', 'USD', 'file', '{}'),
(2, 'Claude Code', 'token', 'USD', 'file', '{}'),
(3, '飞书 AI', 'credit', 'CNY', 'file', '{}'),
(4, 'WorkBuddy/CodeBuddy', 'token', 'CNY', 'file', '{}'),
(5, 'Codex', 'token', 'USD', 'file', '{}'),
(6, 'Trae', 'token', 'CNY', 'file', '{}'),
(7, 'UniAPI', 'token', 'CNY', 'file', '{}'),
(8, 'FastGPT', 'credit', 'CNY', 'file', '{}');

-- ===== AI 模型 =====
INSERT OR IGNORE INTO ai_model (id, tool_id, name, unit_price, unit, tiered_pricing) VALUES
(1, 1, 'Copilot Business', 19.000000, 'seat_month', '{}'),
(2, 2, 'Claude 3.5 Sonnet', 3.000000, '1K_token', '{}'),
(3, 3, '默认套餐', 0.100000, 'credit', '{}'),
(4, 4, '默认模型', 0.050000, '1K_token', '{}'),
(5, 5, 'Codex', 0.002000, '1K_token', '{}'),
(6, 6, 'Trae', 0.010000, '1K_token', '{}'),
(7, 7, 'UniAPI', 0.010000, '1K_call', '{}'),
(8, 8, 'FastGPT', 0.020000, 'credit', '{}');

-- ===== 预警规则 =====
INSERT OR IGNORE INTO alert_rule (id, target_type, target_id, threshold_pct, notify_roles, enabled) VALUES
(1, 'company', 1, 80, 'admin', 1),
(2, 'company', 1, 100, 'admin', 1),
(3, 'department', 1, 80, 'manager,admin', 1),
(4, 'department', 1, 100, 'manager,admin', 1),
(5, 'user', 0, 80, 'employee', 1),
(6, 'user', 0, 100, 'employee,manager', 1);

-- ================================================================
-- 额度记录 — 3个月
-- ================================================================

-- ===== 2026-06 额度 =====
INSERT OR IGNORE INTO quota_record (id, user_id, dept_id, period, quota_type, total_amount, used_amount, remaining_amount, source_application_id) VALUES
-- 研发部
(10, 4, 1, '2026-06', 'monthly_base', 5000.0000, 2800.0000, 2200.0000, NULL),
(11, 5, 1, '2026-06', 'monthly_base', 5000.0000, 1500.0000, 3500.0000, NULL),
(12, 2, 1, '2026-06', 'monthly_base', 8000.0000, 3600.0000, 4400.0000, NULL),
-- 产品部
(13, 6, 2, '2026-06', 'monthly_base', 3000.0000, 1800.0000, 1200.0000, NULL),
(14, 3, 2, '2026-06', 'monthly_base', 5000.0000, 2400.0000, 2600.0000, NULL);

-- ===== 2026-07 额度 =====
INSERT OR IGNORE INTO quota_record (id, user_id, dept_id, period, quota_type, total_amount, used_amount, remaining_amount, source_application_id) VALUES
-- 研发部
(20, 4, 1, '2026-07', 'monthly_base', 5000.0000, 3100.0000, 1900.0000, NULL),
(21, 5, 1, '2026-07', 'monthly_base', 5000.0000, 1700.0000, 3300.0000, NULL),
(22, 2, 1, '2026-07', 'monthly_base', 8000.0000, 3900.0000, 4100.0000, NULL),
-- 产品部
(23, 6, 2, '2026-07', 'monthly_base', 3000.0000, 2000.0000, 1000.0000, NULL),
(24, 3, 2, '2026-07', 'monthly_base', 5000.0000, 2600.0000, 2400.0000, NULL);

-- ===== 2026-08 额度 =====
INSERT OR IGNORE INTO quota_record (id, user_id, dept_id, period, quota_type, total_amount, used_amount, remaining_amount, source_application_id) VALUES
-- 研发部员工月度基础额度
(1, 4, 1, '2026-08', 'monthly_base', 5000.0000, 3200.0000, 1800.0000, NULL),
(2, 5, 1, '2026-08', 'monthly_base', 5000.0000, 1850.0000, 3150.0000, NULL),
(3, 2, 1, '2026-08', 'monthly_base', 8000.0000, 4100.0000, 3900.0000, NULL),
-- 产品部员工月度基础额度
(4, 6, 2, '2026-08', 'monthly_base', 3000.0000, 2100.0000, 900.0000, NULL),
(5, 3, 2, '2026-08', 'monthly_base', 5000.0000, 2800.0000, 2200.0000, NULL),
-- 追加额度（通过审批）
(6, 4, 1, '2026-08', 'additional', 2000.0000, 0.0000, 2000.0000, 1),
(7, 6, 2, '2026-08', 'additional', 1000.0000, 500.0000, 500.0000, 2);

-- ================================================================
-- 额度申请记录 — 3个月
-- ================================================================

INSERT OR IGNORE INTO quota_application (id, applicant_id, dept_id, type, amount, reason, status, approver_id, approve_comment, created_at, updated_at) VALUES
-- 2026-06 申请
(10, 4, 1, 'additional', 1000.0000, '6月项目收尾，Claude Code 用量增加', 'admin_approved', 1, '同意，注意控制', '2026-06-10 09:00:00', '2026-06-10 14:00:00'),
(11, 5, 1, 'additional', 800.0000, 'WorkBuddy 代码审查需求', 'manager_approved', 2, '同意', '2026-06-12 10:00:00', '2026-06-12 11:00:00'),
(12, 6, 2, 'additional', 500.0000, '飞书AI文档生成', 'rejected', 3, '预算紧张，下月再申请', '2026-06-15 14:00:00', '2026-06-15 16:00:00'),
-- 2026-07 申请
(13, 4, 1, 'additional', 1500.0000, '7月新项目启动，Codex 调用量大', 'admin_approved', 1, '批准追加', '2026-07-05 10:00:00', '2026-07-05 15:00:00'),
(14, 2, 1, 'additional', 2000.0000, '团队整体用量超标，申请部门追加', 'admin_approved', 1, '同意', '2026-07-10 11:00:00', '2026-07-10 16:00:00'),
(15, 6, 2, 'additional', 600.0000, 'FastGPT 知识库构建', 'manager_approved', 3, '同意', '2026-07-08 09:00:00', '2026-07-08 10:00:00'),
(16, 3, 2, 'temporary', 1000.0000, '产品发布月临时额度', 'admin_approved', 1, '批准', '2026-07-15 14:00:00', '2026-07-15 17:00:00'),
-- 2026-08 申请
(1, 4, 1, 'additional', 2000.0000, '项目紧急，Claude Code 用量超出预期，申请追加额度', 'admin_approved', 1, '同意追加，注意控制用量', '2026-08-05 10:30:00', '2026-08-06 14:20:00'),
(2, 6, 2, 'additional', 1000.0000, '飞书AI积分不足，需补充', 'admin_approved', 1, '批准', '2026-08-07 09:15:00', '2026-08-07 16:00:00'),
(3, 5, 1, 'additional', 1500.0000, 'Codex 调用量大，申请追加', 'pending', NULL, NULL, '2026-08-12 11:00:00', '2026-08-12 11:00:00'),
(4, 2, 1, 'additional', 3000.0000, '团队整体用量超标，申请部门追加', 'pending_admin', 2, '部门预算充足，同意升级至管理员审批', '2026-08-11 15:30:00', '2026-08-11 17:00:00'),
(5, 6, 2, 'additional', 800.0000, 'FastGPT 知识库构建需要额外积分', 'rejected', 3, '当前预算紧张，建议下月再申请', '2026-08-09 14:00:00', '2026-08-09 16:30:00'),
(6, 4, 1, 'additional', 500.0000, 'GitHub Copilot 席位调整', 'manager_approved', 2, '同意', '2026-08-08 09:00:00', '2026-08-08 10:15:00');

-- ================================================================
-- 消费记录 — 3个月，每个用户有不同的消费模式
-- ================================================================

-- ===== 2026-06 消费记录 =====
INSERT OR IGNORE INTO usage_record (id, user_id, dept_id, tool_id, model_id, usage_date, usage_quantity, original_amount, original_currency, amount_cny, source, remark, raw_data) VALUES
-- employee01 (user 4, 研发部) — 6月: 重度 Claude Code 用户
(30, 4, 1, 1, 1, '2026-06-01', 1.0000, 19.0000, 'USD', 134.9000, 'import', 'Copilot 月度席位', NULL),
(31, 4, 1, 2, 2, '2026-06-03', 600.0000, 1800.0000, 'USD', 12780.0000, 'import', 'Claude Code 代码生成', NULL),
(32, 4, 1, 5, 5, '2026-06-05', 8000.0000, 16.0000, 'USD', 113.6000, 'import', 'Codex 辅助', NULL),
(33, 4, 1, 6, 6, '2026-06-08', 300.0000, 3.0000, 'CNY', 3.0000, 'import', 'Trae 少量使用', NULL),
-- employee02 (user 5, 研发部) — 6月: WorkBuddy + UniAPI 为主
(34, 5, 1, 1, 1, '2026-06-02', 1.0000, 19.0000, 'USD', 134.9000, 'import', 'Copilot 月度席位', NULL),
(35, 5, 1, 4, 4, '2026-06-04', 2500.0000, 125.0000, 'CNY', 125.0000, 'import', 'WorkBuddy 代码审查', NULL),
(36, 5, 1, 7, 7, '2026-06-06', 1500.0000, 15.0000, 'CNY', 15.0000, 'import', 'UniAPI 接口调用', NULL),
(37, 5, 1, 4, 4, '2026-06-10', 1000.0000, 50.0000, 'CNY', 50.0000, 'import', 'WorkBuddy 补充', NULL),
-- manager_rd (user 2, 研发部) — 6月: 管理+少量编码
(38, 2, 1, 1, 1, '2026-06-01', 1.0000, 19.0000, 'USD', 134.9000, 'import', 'Copilot 月度席位', NULL),
(39, 2, 1, 2, 2, '2026-06-05', 300.0000, 900.0000, 'USD', 6390.0000, 'import', 'Claude Code 管理调用', NULL),
(40, 2, 1, 3, 3, '2026-06-07', 150.0000, 15.0000, 'CNY', 15.0000, 'import', '飞书AI会议纪要', NULL),
-- employee03 (user 6, 产品部) — 6月: 飞书AI + FastGPT
(41, 6, 2, 3, 3, '2026-06-02', 4000.0000, 400.0000, 'CNY', 400.0000, 'import', '飞书AI文档生成', NULL),
(42, 6, 2, 8, 8, '2026-06-04', 6000.0000, 120.0000, 'CNY', 120.0000, 'import', 'FastGPT 知识库', NULL),
(43, 6, 2, 1, 1, '2026-06-06', 1.0000, 19.0000, 'USD', 134.9000, 'import', 'Copilot 月度席位', NULL),
-- manager_pm (user 3, 产品部) — 6月: 飞书AI + Claude
(44, 3, 2, 1, 1, '2026-06-01', 1.0000, 19.0000, 'USD', 134.9000, 'import', 'Copilot 月度席位', NULL),
(45, 3, 2, 3, 3, '2026-06-05', 800.0000, 80.0000, 'CNY', 80.0000, 'import', '飞书AI会议纪要', NULL),
(46, 3, 2, 2, 2, '2026-06-08', 50.0000, 150.0000, 'USD', 1065.0000, 'import', 'Claude Code 需求分析', NULL),
-- 部门公共消费
(47, NULL, 1, 2, 2, '2026-06-07', 150.0000, 450.0000, 'USD', 3195.0000, 'import', 'Claude Code 部门公共', NULL),
(48, NULL, 2, 3, 3, '2026-06-06', 500.0000, 50.0000, 'CNY', 50.0000, 'import', '飞书AI部门公共', NULL);

-- ===== 2026-07 消费记录 =====
INSERT OR IGNORE INTO usage_record (id, user_id, dept_id, tool_id, model_id, usage_date, usage_quantity, original_amount, original_currency, amount_cny, source, remark, raw_data) VALUES
-- employee01 (user 4, 研发部) — 7月: 新项目启动，Codex 用量激增
(50, 4, 1, 1, 1, '2026-07-01', 1.0000, 19.0000, 'USD', 134.9000, 'import', 'Copilot 月度席位', NULL),
(51, 4, 1, 2, 2, '2026-07-03', 700.0000, 2100.0000, 'USD', 14910.0000, 'import', 'Claude Code 项目开发', NULL),
(52, 4, 1, 5, 5, '2026-07-05', 15000.0000, 30.0000, 'USD', 213.0000, 'import', 'Codex 代码生成激增', NULL),
(53, 4, 1, 6, 6, '2026-07-08', 400.0000, 4.0000, 'CNY', 4.0000, 'import', 'Trae 辅助', NULL),
(54, 4, 1, 2, 2, '2026-07-10', 100.0000, 300.0000, 'USD', 2130.0000, 'import', 'Claude Code 追加', NULL),
-- employee02 (user 5, 研发部) — 7月: WorkBuddy 为主，少量 UniAPI
(55, 5, 1, 1, 1, '2026-07-02', 1.0000, 19.0000, 'USD', 134.9000, 'import', 'Copilot 月度席位', NULL),
(56, 5, 1, 4, 4, '2026-07-04', 2800.0000, 140.0000, 'CNY', 140.0000, 'import', 'WorkBuddy 代码审查', NULL),
(57, 5, 1, 7, 7, '2026-07-06', 1800.0000, 18.0000, 'CNY', 18.0000, 'import', 'UniAPI 接口调用', NULL),
(58, 5, 1, 4, 4, '2026-07-09', 800.0000, 40.0000, 'CNY', 40.0000, 'import', 'WorkBuddy 补充', NULL),
-- manager_rd (user 2, 研发部) — 7月: 团队管理，Claude 用量增加
(59, 2, 1, 1, 1, '2026-07-01', 1.0000, 19.0000, 'USD', 134.9000, 'import', 'Copilot 月度席位', NULL),
(60, 2, 1, 2, 2, '2026-07-05', 400.0000, 1200.0000, 'USD', 8520.0000, 'import', 'Claude Code 团队管理', NULL),
(61, 2, 1, 3, 3, '2026-07-07', 180.0000, 18.0000, 'CNY', 18.0000, 'import', '飞书AI会议纪要', NULL),
(62, 2, 1, 6, 6, '2026-07-10', 200.0000, 2.0000, 'CNY', 2.0000, 'import', 'Trae 代码审查', NULL),
-- employee03 (user 6, 产品部) — 7月: FastGPT 知识库建设
(63, 6, 2, 3, 3, '2026-07-02', 4500.0000, 450.0000, 'CNY', 450.0000, 'import', '飞书AI文档生成', NULL),
(64, 6, 2, 8, 8, '2026-07-04', 7000.0000, 140.0000, 'CNY', 140.0000, 'import', 'FastGPT 知识库问答', NULL),
(65, 6, 2, 1, 1, '2026-07-06', 1.0000, 19.0000, 'USD', 134.9000, 'import', 'Copilot 月度席位', NULL),
(66, 6, 2, 8, 8, '2026-07-09', 3000.0000, 60.0000, 'CNY', 60.0000, 'import', 'FastGPT 追加', NULL),
-- manager_pm (user 3, 产品部) — 7月: 产品发布月
(67, 3, 2, 1, 1, '2026-07-01', 1.0000, 19.0000, 'USD', 134.9000, 'import', 'Copilot 月度席位', NULL),
(68, 3, 2, 3, 3, '2026-07-05', 1200.0000, 120.0000, 'CNY', 120.0000, 'import', '飞书AI产品发布', NULL),
(69, 3, 2, 2, 2, '2026-07-08', 70.0000, 210.0000, 'USD', 1491.0000, 'import', 'Claude Code 需求分析', NULL),
(70, 3, 2, 8, 8, '2026-07-10', 2500.0000, 50.0000, 'CNY', 50.0000, 'import', 'FastGPT 产品文档', NULL),
-- 部门公共消费
(71, NULL, 1, 2, 2, '2026-07-07', 180.0000, 540.0000, 'USD', 3834.0000, 'import', 'Claude Code 部门公共', NULL),
(72, NULL, 2, 3, 3, '2026-07-06', 600.0000, 60.0000, 'CNY', 60.0000, 'import', '飞书AI部门公共', NULL);

-- ===== 2026-08 消费记录 =====
INSERT OR IGNORE INTO usage_record (id, user_id, dept_id, tool_id, model_id, usage_date, usage_quantity, original_amount, original_currency, amount_cny, source, remark, raw_data) VALUES
-- employee01 (user 4, 研发部) — 8月: Claude Code 重度用户
(1, 4, 1, 1, 1, '2026-08-01', 1.0000, 19.0000, 'USD', 134.9000, 'import', 'Copilot 月度席位', NULL),
(2, 4, 1, 2, 2, '2026-08-03', 850.0000, 2550.0000, 'USD', 18105.0000, 'import', 'Claude Code token 消费', NULL),
(3, 4, 1, 5, 5, '2026-08-05', 12000.0000, 24.0000, 'USD', 170.4000, 'import', 'Codex 代码生成', NULL),
(4, 4, 1, 6, 6, '2026-08-08', 500.0000, 5.0000, 'CNY', 5.0000, 'import', 'Trae 辅助编程', NULL),
(5, 4, 1, 2, 2, '2026-08-10', 120.0000, 360.0000, 'USD', 2556.0000, 'import', 'Claude Code 追加调用', NULL),
-- employee02 (user 5, 研发部) — 8月: WorkBuddy + UniAPI
(6, 5, 1, 1, 1, '2026-08-02', 1.0000, 19.0000, 'USD', 134.9000, 'import', 'Copilot 月度席位', NULL),
(7, 5, 1, 4, 4, '2026-08-04', 3000.0000, 150.0000, 'CNY', 150.0000, 'import', 'WorkBuddy 代码审查', NULL),
(8, 5, 1, 5, 5, '2026-08-06', 8000.0000, 16.0000, 'USD', 113.6000, 'import', 'Codex 调用', NULL),
(9, 5, 1, 7, 7, '2026-08-09', 2000.0000, 20.0000, 'CNY', 20.0000, 'import', 'UniAPI 接口调用', NULL),
(10, 5, 1, 2, 2, '2026-08-11', 60.0000, 180.0000, 'USD', 1278.0000, 'import', 'Claude Code 少量调用', NULL),
-- manager_rd (user 2, 研发部) — 8月: 管理调用
(11, 2, 1, 1, 1, '2026-08-01', 1.0000, 19.0000, 'USD', 134.9000, 'import', 'Copilot 月度席位', NULL),
(12, 2, 1, 2, 2, '2026-08-05', 500.0000, 1500.0000, 'USD', 10650.0000, 'import', 'Claude Code 团队管理调用', NULL),
(13, 2, 1, 3, 3, '2026-08-07', 200.0000, 20.0000, 'CNY', 20.0000, 'import', '飞书AI会议纪要', NULL),
(14, 2, 1, 6, 6, '2026-08-10', 300.0000, 3.0000, 'CNY', 3.0000, 'import', 'Trae 代码审查', NULL),
-- employee03 (user 6, 产品部) — 8月: 飞书AI + FastGPT
(15, 6, 2, 3, 3, '2026-08-02', 5000.0000, 500.0000, 'CNY', 500.0000, 'import', '飞书AI文档生成', NULL),
(16, 6, 2, 8, 8, '2026-08-04', 8000.0000, 160.0000, 'CNY', 160.0000, 'import', 'FastGPT 知识库问答', NULL),
(17, 6, 2, 1, 1, '2026-08-06', 1.0000, 19.0000, 'USD', 134.9000, 'import', 'Copilot 月度席位', NULL),
(18, 6, 2, 3, 3, '2026-08-09', 3000.0000, 300.0000, 'CNY', 300.0000, 'import', '飞书AI智能助手', NULL),
(19, 6, 2, 8, 8, '2026-08-12', 5000.0000, 100.0000, 'CNY', 100.0000, 'import', 'FastGPT 追加调用', NULL),
-- manager_pm (user 3, 产品部) — 8月: 需求分析
(20, 3, 2, 1, 1, '2026-08-01', 1.0000, 19.0000, 'USD', 134.9000, 'import', 'Copilot 月度席位', NULL),
(21, 3, 2, 3, 3, '2026-08-05', 1000.0000, 100.0000, 'CNY', 100.0000, 'import', '飞书AI会议纪要', NULL),
(22, 3, 2, 2, 2, '2026-08-08', 80.0000, 240.0000, 'USD', 1704.0000, 'import', 'Claude Code 需求分析', NULL),
(23, 3, 2, 8, 8, '2026-08-10', 3000.0000, 60.0000, 'CNY', 60.0000, 'import', 'FastGPT 产品文档', NULL),
-- 部门公共消费
(24, NULL, 1, 2, 2, '2026-08-07', 200.0000, 600.0000, 'USD', 4260.0000, 'import', 'Claude Code 部门公共调用', NULL),
(25, NULL, 2, 3, 3, '2026-08-06', 800.0000, 80.0000, 'CNY', 80.0000, 'import', '飞书AI部门公共使用', NULL);

-- ===== 预警日志 =====
INSERT OR IGNORE INTO alert_log (id, rule_id, target_type, target_id, actual_pct, message, created_at) VALUES
(1, 5, 'user', 6, 85, '用户 employee03 使用率达到 85%，超过阈值 80%', '2026-08-09 10:00:00'),
(2, 3, 'department', 1, 39, '部门 研发部 使用率达到 39%，未超过阈值 80%', '2026-08-10 08:00:00'),
(3, 5, 'user', 4, 64, '用户 employee01 使用率达到 64%，未超过阈值 80%', '2026-08-08 08:00:00'),
(4, 1, 'company', 1, 5, '公司使用率达到 5%，未超过阈值 80%', '2026-08-10 08:00:00'),
(5, 5, 'user', 4, 62, '用户 employee01 7月使用率 62%', '2026-07-31 08:00:00'),
(6, 5, 'user', 6, 67, '用户 employee03 7月使用率 67%', '2026-07-31 08:00:00'),
(7, 3, 'department', 2, 52, '部门 产品部 7月使用率 52%', '2026-07-31 08:00:00');
