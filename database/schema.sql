-- =============================================================================
-- TechLab Inventory & Service Desk — Esquema de Banco de Dados Relacional
-- Disciplina: Programação Orientada a Objetos & Persistência (2026.2)
-- Professor: Me. Tibério Bezerra Soares | Aluno: Jeronimo Silva
-- Banco de Dados Homologado: SQLite 3 / MySQL 8.0
-- =============================================================================

-- Tabela de Ativos de Tecnologia (Mapeamento de Herança TPH - Table Per Hierarchy)
CREATE TABLE IF NOT EXISTS ativos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    tipo VARCHAR(30) NOT NULL,               -- 'Servidor', 'EquipamentoRede', 'EstacaoTrabalho'
    patrimonio VARCHAR(50) UNIQUE NOT NULL,  -- Código de tombamento único (ex: PAT-2026-001)
    modelo VARCHAR(100) NOT NULL,            -- Modelo do equipamento
    data_aquisicao VARCHAR(20),              -- Data de aquisição (formato YYYY-MM-DD)
    status VARCHAR(20) DEFAULT 'Ativo',      -- 'Ativo', 'EmManutencao', 'Descartado'
    
    -- Atributos específicos de Servidor
    ip_estatico VARCHAR(45),
    so VARCHAR(50),
    memoria_ram INT,
    
    -- Atributos específicos de Equipamento de Rede
    quantidade_portas INT,
    tipo_rede VARCHAR(30),
    velocidade_mbps INT,
    
    -- Atributos específicos de Estação de Trabalho
    departamento VARCHAR(50),
    usuario VARCHAR(100),
    gpu_dedicada BOOLEAN
);

-- Tabela de Chamados do Service Desk (Associação com AtivoTI)
CREATE TABLE IF NOT EXISTS chamados (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    titulo VARCHAR(100) NOT NULL,
    descricao TEXT,
    prioridade VARCHAR(20) NOT NULL,         -- 'Baixa', 'Media', 'Alta'
    data_abertura VARCHAR(20),
    aberto BOOLEAN DEFAULT 1,                -- 1 para Aberto, 0 para Fechado
    ativo_id INTEGER,
    FOREIGN KEY (ativo_id) REFERENCES ativos(id) ON DELETE SET NULL
);

-- Índices para otimização de consultas
CREATE INDEX IF NOT EXISTS idx_ativos_patrimonio ON ativos(patrimonio);
CREATE INDEX IF NOT EXISTS idx_chamados_ativo ON chamados(ativo_id);
