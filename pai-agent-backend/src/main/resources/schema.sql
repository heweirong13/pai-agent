-- PaiAgent 数据库初始化脚本 (H2 Compatible)

-- =====================================================
-- 1. 工作流定义表
-- =====================================================
CREATE TABLE IF NOT EXISTS workflow_definition (
    id BIGINT PRIMARY KEY,
    workflow_name VARCHAR(100) NOT NULL,
    workflow_desc VARCHAR(500),
    version INT DEFAULT 1,
    workflow_graph JSON,
    status VARCHAR(20) DEFAULT 'DRAFT',
    created_by VARCHAR(50),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_status ON workflow_definition(status);
CREATE INDEX IF NOT EXISTS idx_created_time ON workflow_definition(created_time);

-- =====================================================
-- 2. 工作流节点表
-- =====================================================
CREATE TABLE IF NOT EXISTS workflow_node (
    id BIGINT PRIMARY KEY,
    workflow_id BIGINT NOT NULL,
    node_id VARCHAR(50) NOT NULL,
    node_type VARCHAR(20) NOT NULL,
    node_name VARCHAR(100),
    node_config JSON,
    position_x DOUBLE,
    position_y DOUBLE,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_workflow_id ON workflow_node(workflow_id);
CREATE INDEX IF NOT EXISTS idx_node_id ON workflow_node(node_id);

-- =====================================================
-- 3. 工作流边表（连接线）
-- =====================================================
CREATE TABLE IF NOT EXISTS workflow_edge (
    id BIGINT PRIMARY KEY,
    workflow_id BIGINT NOT NULL,
    edge_id VARCHAR(50) NOT NULL,
    source_node_id VARCHAR(50) NOT NULL,
    target_node_id VARCHAR(50) NOT NULL,
    source_handle VARCHAR(50),
    target_handle VARCHAR(50),
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_edge_workflow_id ON workflow_edge(workflow_id);
CREATE INDEX IF NOT EXISTS idx_source_node ON workflow_edge(source_node_id);
CREATE INDEX IF NOT EXISTS idx_target_node ON workflow_edge(target_node_id);

-- =====================================================
-- 4. 执行记录表
-- =====================================================
CREATE TABLE IF NOT EXISTS execution_record (
    id BIGINT PRIMARY KEY,
    workflow_id BIGINT NOT NULL,
    execution_id VARCHAR(50) NOT NULL,
    input_data JSON,
    output_data JSON,
    execution_status VARCHAR(20) DEFAULT 'PENDING',
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    duration BIGINT,
    error_message TEXT,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_execution_id ON execution_record(execution_id);
CREATE INDEX IF NOT EXISTS idx_record_workflow_id ON execution_record(workflow_id);
CREATE INDEX IF NOT EXISTS idx_record_status ON execution_record(execution_status);
CREATE INDEX IF NOT EXISTS idx_record_created_time ON execution_record(created_time);

-- =====================================================
-- 5. 节点执行日志表
-- =====================================================
CREATE TABLE IF NOT EXISTS node_execution_log (
    id BIGINT PRIMARY KEY,
    execution_record_id BIGINT NOT NULL,
    execution_id VARCHAR(50) NOT NULL,
    node_id VARCHAR(50) NOT NULL,
    node_type VARCHAR(20),
    node_name VARCHAR(100),
    input_data JSON,
    output_data JSON,
    execution_status VARCHAR(20) DEFAULT 'PENDING',
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    duration BIGINT,
    error_message TEXT,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_log_execution_id ON node_execution_log(execution_id);
CREATE INDEX IF NOT EXISTS idx_log_execution_record_id ON node_execution_log(execution_record_id);
CREATE INDEX IF NOT EXISTS idx_log_node_id ON node_execution_log(node_id);

-- =====================================================
-- 6. 大模型配置表
-- =====================================================
CREATE TABLE IF NOT EXISTS llm_provider_config (
    id BIGINT PRIMARY KEY,
    provider_name VARCHAR(50) NOT NULL,
    display_name VARCHAR(100) NOT NULL,
    api_key VARCHAR(500),
    api_endpoint VARCHAR(200),
    model_list JSON,
    is_enabled TINYINT DEFAULT 1,
    sort_order INT DEFAULT 0,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_provider_name ON llm_provider_config(provider_name);

-- =====================================================
-- 7. 音频文件表
-- =====================================================
CREATE TABLE IF NOT EXISTS audio_file (
    id BIGINT PRIMARY KEY,
    execution_record_id BIGINT,
    execution_id VARCHAR(50),
    file_name VARCHAR(200) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    duration DOUBLE,
    file_url VARCHAR(500),
    format VARCHAR(20) DEFAULT 'mp3',
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_audio_execution_id ON audio_file(execution_id);
CREATE INDEX IF NOT EXISTS idx_audio_created_time ON audio_file(created_time);

-- =====================================================
-- 初始化大模型配置数据
-- =====================================================
MERGE INTO llm_provider_config (id, provider_name, display_name, api_endpoint, model_list, is_enabled, sort_order)
KEY (id)
VALUES
(1, 'DEEPSEEK', 'DeepSeek', 'https://api.deepseek.com/v1', '["deepseek-chat", "deepseek-coder"]', 1, 1),
(2, 'QWEN', '通义千问', 'https://dashscope.aliyuncs.com/compatible-mode/v1', '["qwen-turbo", "qwen-plus", "qwen-max"]', 1, 2),
(3, 'OPENAI', 'OpenAI', 'https://api.openai.com/v1', '["gpt-3.5-turbo", "gpt-4", "gpt-4-turbo"]', 1, 3),
(4, 'GLM', '智谱AI', 'https://open.bigmodel.cn/api/paas/v4', '["glm-4", "glm-4-flash", "glm-3-turbo"]', 1, 4);
