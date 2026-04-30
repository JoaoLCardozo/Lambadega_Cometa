-- Script de criação do banco de dados Lambadega Cometa para PostgreSQL

-- ============================================================
-- CRIAÇÃO DO BANCO DE DADOS
-- ============================================================
DROP DATABASE IF EXISTS LambadegaCometa;
CREATE DATABASE LambadegaCometa
  WITH ENCODING 'UTF8'
  TEMPLATE template0;

\c LambadegaCometa;

-- ============================================================
-- USUARIO
-- ============================================================
CREATE TABLE usuario (
    id               SERIAL PRIMARY KEY,
    nome             VARCHAR(100) NOT NULL,
    email            VARCHAR(100),
    usuario          VARCHAR(50)  NOT NULL UNIQUE,
    senha            VARCHAR(100) NOT NULL,
    ativo            BOOLEAN      NOT NULL DEFAULT TRUE,
    data_criacao     TIMESTAMP    NOT NULL DEFAULT NOW(),
    data_atualizacao TIMESTAMP
);

-- ============================================================
-- CLIENTE
-- ============================================================
CREATE TABLE cliente (
    id                 SERIAL PRIMARY KEY,
	tipo_pessoa CHAR(1) NOT NULL CHECK (tipo_pessoa IN ('F','J')),
    nome_razao_social       VARCHAR(150) NOT NULL,
    nome_fantasia      VARCHAR(150),
    documento               VARCHAR(18)  NOT NULL UNIQUE,
    inscricao_estadual VARCHAR(20),
    logradouro         VARCHAR(150),
    numero             VARCHAR(10),
    complemento        VARCHAR(100),
    bairro             VARCHAR(100),
    municipio          VARCHAR(100),
    uf                 CHAR(2),
    cep                VARCHAR(9),
    telefone           VARCHAR(20),
    email              VARCHAR(100),
    status             VARCHAR(10)  NOT NULL DEFAULT 'ATIVO' CHECK (status IN ('ATIVO','INATIVO'))
);

-- ============================================================
-- MOTORISTA
-- ============================================================
CREATE TABLE motorista (
    id               SERIAL PRIMARY KEY,
    nome             VARCHAR(100) NOT NULL,
    cpf              VARCHAR(11)  NOT NULL UNIQUE,
    data_nascimento  DATE,
    telefone         VARCHAR(13),
    cnh_numero       VARCHAR(20)  NOT NULL,
    cnh_categoria    CHAR(1)      NOT NULL CHECK (cnh_categoria IN ('A','B','C','D','E')),
    cnh_validade     DATE         NOT NULL,
    tipo_vinculo     VARCHAR(20)  NOT NULL CHECK (tipo_vinculo IN ('FUNCIONARIO','AGREGADO','TERCEIRO')),
    status           VARCHAR(10)  NOT NULL DEFAULT 'ATIVO' CHECK (status IN ('ATIVO','INATIVO','SUSPENSO'))
);

-- ============================================================
-- VEICULO
-- ============================================================
CREATE TABLE veiculo (
    id               SERIAL PRIMARY KEY,
    placa            VARCHAR(8)   NOT NULL UNIQUE,
    rntrc            VARCHAR(20),
    ano_fabricacao   INTEGER,
    tipo             VARCHAR(20)  NOT NULL CHECK (tipo IN ('TRUCK','CARRETA','VAN','UTILITARIO')),
    tara_kg          NUMERIC(10,2),
    capacidade_kg    NUMERIC(10,2),
    volume_m3        NUMERIC(10,2),
    status           VARCHAR(20)  NOT NULL DEFAULT 'DISPONIVEL' CHECK (status IN ('DISPONIVEL','EM_VIAGEM','EM_MANUTENCAO'))
);

-- ============================================================
-- FRETE
-- ============================================================
CREATE TABLE frete (
    id                     SERIAL PRIMARY KEY,
    numero                 VARCHAR(15)    NOT NULL UNIQUE,
    id_remetente           INTEGER        NOT NULL REFERENCES cliente(id),
    id_destinatario        INTEGER        NOT NULL REFERENCES cliente(id),
    id_motorista           INTEGER        NOT NULL REFERENCES motorista(id),
    id_veiculo             INTEGER        NOT NULL REFERENCES veiculo(id),
    municipio_origem       VARCHAR(100)   NOT NULL,
    uf_origem              CHAR(2)        NOT NULL,
    municipio_destino      VARCHAR(100)   NOT NULL,
    uf_destino             CHAR(2)        NOT NULL,
    descricao_carga        VARCHAR(255),
    peso_kg                NUMERIC(10,2),
    volumes                INTEGER,
    valor_frete            NUMERIC(12,2),
    aliquota_icms          NUMERIC(5,2),
    valor_icms             NUMERIC(12,2),
    valor_total            NUMERIC(12,2),
    status                 VARCHAR(20)    NOT NULL DEFAULT 'EMITIDO'
                               CHECK (status IN ('EMITIDO','SAIDA_CONFIRMADA','EM_TRANSITO','ENTREGUE','NAO_ENTREGUE','CANCELADO')),
    data_emissao           TIMESTAMP      NOT NULL DEFAULT NOW(),
    data_previsao_entrega  DATE           NOT NULL,
    data_saida             TIMESTAMP,
    data_entrega           TIMESTAMP
);

-- ============================================================
-- OCORRENCIA_FRETE
-- ============================================================
CREATE TABLE ocorrencia_frete (
    id                  SERIAL PRIMARY KEY,
    id_frete            INTEGER      NOT NULL REFERENCES frete(id),
    tipo                VARCHAR(30)  NOT NULL
                            CHECK (tipo IN ('SAIDA_DO_PATIO','EM_ROTA','TENTATIVA_ENTREGA',
                                            'ENTREGA_REALIZADA','AVARIA','EXTRAVIO','OUTROS')),
    data_hora           TIMESTAMP    NOT NULL,
    municipio           VARCHAR(100),
    uf                  CHAR(2),
    descricao           TEXT,
    nome_recebedor      VARCHAR(100),
    documento_recebedor VARCHAR(20)
);

-- USUARIO (senha: 123456 — em produção usar hash)
INSERT INTO usuario (nome, email, usuario, senha, ativo) VALUES
('Administrador', 'admin@lambadegacometa.com.br', 'admin', '123456', TRUE);

-- CLIENTES
INSERT INTO cliente (tipo_pessoa, nome_razao_social, nome_fantasia, documento, inscricao_estadual,
    logradouro, numero, bairro, municipio, uf, cep, telefone, email, status) VALUES
('J', 'Indústrias Recife S.A.', 'Ind. Recife', '11222333000144', '111222333',
    'Av. Caxangá', '1000', 'Cordeiro', 'Recife', 'PE', '50000000', '8133331111', 'contato@indrecife.com.br', 'ATIVO'),
('J', 'Distribuidora Nordeste Ltda.', 'Dist. Nordeste', '22333444000155', '222333444',
    'Rua das Flores', '200', 'Centro', 'Caruaru', 'PE', '55000000', '8133332222', 'contato@distnordeste.com.br', 'ATIVO'),
('F', 'João da Silva', NULL, '12345678901', NULL,
    'Rua Exemplo', '10', 'Boa Vista', 'Recife', 'PE', '50010000', '81999990000', 'joao@email.com', 'ATIVO');

-- MOTORISTAS
INSERT INTO motorista (nome, cpf, data_nascimento, telefone,
    cnh_numero, cnh_categoria, cnh_validade, tipo_vinculo, status) VALUES
('Carlos Alberto Silva',  '11122233344', '1985-03-15', '81999991111',
    'CNH001111', 'E', '2027-03-15', 'FUNCIONARIO', 'ATIVO'),
('José Ferreira Santos',  '22233344455', '1979-07-22', '81999992222',
    'CNH002222', 'D', '2026-07-22', 'AGREGADO',    'ATIVO'),
('Pedro Henrique Lima',   '33344455566', '1990-11-05', '81999993333',
    'CNH003333', 'C', '2025-01-01', 'TERCEIRO',    'ATIVO');

-- VEICULOS
INSERT INTO veiculo (placa, rntrc, ano_fabricacao, tipo,
    tara_kg, capacidade_kg, volume_m3, status) VALUES
('ABC1D23', 'RNTRC001', 2020, 'CARRETA',   8000, 25000, 90.0, 'DISPONIVEL'),
('DEF2E34', 'RNTRC002', 2018, 'TRUCK',     6000, 14000, 45.0, 'DISPONIVEL'),
('GHI3F45', 'RNTRC003', 2022, 'VAN',       2000,  3000, 12.0, 'DISPONIVEL');

-- FRETES (5 fretes em diferentes status)

-- 1: EMITIDO
INSERT INTO frete (numero, id_remetente, id_destinatario, id_motorista, id_veiculo,
    municipio_origem, uf_origem, municipio_destino, uf_destino,
    descricao_carga, peso_kg, volumes,
    valor_frete, aliquota_icms, valor_icms, valor_total,
    status, data_emissao, data_previsao_entrega) VALUES
('FRT-2026-00001', 1, 2, 1, 1,
    'Recife', 'PE', 'Caruaru', 'PE',
    'Peças industriais', 5000.00, 10,
    1200.00, 12.00, 144.00, 1344.00,
    'EMITIDO', NOW(), CURRENT_DATE + 3);

-- 2: SAIDA_CONFIRMADA
INSERT INTO frete (numero, id_remetente, id_destinatario, id_motorista, id_veiculo,
    municipio_origem, uf_origem, municipio_destino, uf_destino,
    descricao_carga, peso_kg, volumes,
    valor_frete, aliquota_icms, valor_icms, valor_total,
    status, data_emissao, data_previsao_entrega, data_saida) VALUES
('FRT-2026-00002', 2, 3, 2, 2,
    'Caruaru', 'PE', 'Recife', 'PE',
    'Produtos alimentícios', 8000.00, 20,
    1800.00, 12.00, 216.00, 2016.00,
    'SAIDA_CONFIRMADA', NOW() - INTERVAL '1 day', CURRENT_DATE + 2,
    NOW() - INTERVAL '6 hours');

-- 3: EM_TRANSITO
INSERT INTO frete (numero, id_remetente, id_destinatario, id_motorista, id_veiculo,
    municipio_origem, uf_origem, municipio_destino, uf_destino,
    descricao_carga, peso_kg, volumes,
    valor_frete, aliquota_icms, valor_icms, valor_total,
    status, data_emissao, data_previsao_entrega, data_saida) VALUES
('FRT-2026-00003', 1, 3, 3, 3,
    'Recife', 'PE', 'Olinda', 'PE',
    'Materiais de construção', 2500.00, 5,
    600.00, 12.00, 72.00, 672.00,
    'EM_TRANSITO', NOW() - INTERVAL '2 days', CURRENT_DATE + 1,
    NOW() - INTERVAL '1 day');

-- 4: ENTREGUE
INSERT INTO frete (numero, id_remetente, id_destinatario, id_motorista, id_veiculo,
    municipio_origem, uf_origem, municipio_destino, uf_destino,
    descricao_carga, peso_kg, volumes,
    valor_frete, aliquota_icms, valor_icms, valor_total,
    status, data_emissao, data_previsao_entrega, data_saida, data_entrega) VALUES
('FRT-2026-00004', 2, 1, 1, 1,
    'Caruaru', 'PE', 'Recife', 'PE',
    'Tecidos e confecções', 3000.00, 15,
    900.00, 12.00, 108.00, 1008.00,
    'ENTREGUE', NOW() - INTERVAL '5 days', CURRENT_DATE - 1,
    NOW() - INTERVAL '4 days', NOW() - INTERVAL '1 day');

-- 5: CANCELADO
INSERT INTO frete (numero, id_remetente, id_destinatario, id_motorista, id_veiculo,
    municipio_origem, uf_origem, municipio_destino, uf_destino,
    descricao_carga, peso_kg, volumes,
    valor_frete, aliquota_icms, valor_icms, valor_total,
    status, data_emissao, data_previsao_entrega) VALUES
('FRT-2026-00005', 3, 2, 2, 2,
    'Recife', 'PE', 'Caruaru', 'PE',
    'Equipamentos eletrônicos', 1500.00, 8,
    1500.00, 12.00, 180.00, 1680.00,
    'CANCELADO', NOW() - INTERVAL '3 days', CURRENT_DATE + 5);

-- OCORRÊNCIAS para o frete EM_TRANSITO (id=3)
INSERT INTO ocorrencia_frete (id_frete, tipo, data_hora, municipio, uf, descricao) VALUES
(3, 'SAIDA_DO_PATIO',   NOW() - INTERVAL '1 day 8 hours',  'Recife', 'PE', 'Veículo saiu do pátio'),
(3, 'EM_ROTA',          NOW() - INTERVAL '1 day 4 hours',  'Recife', 'PE', 'Veículo em rota normal'),
(3, 'TENTATIVA_ENTREGA',NOW() - INTERVAL '2 hours',        'Olinda', 'PE', 'Tentativa de entrega — destinatário ausente');
