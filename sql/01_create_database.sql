-- Script de criação do banco de dados Lambadega Cometa para PostgreSQL
-- Data: 23 de abril de 2026

-- ============================================================
-- CRIAÇÃO DO BANCO DE DADOS
-- ============================================================
DROP DATABASE IF EXISTS lambadega_cometa;
CREATE DATABASE lambadega_cometa
  WITH ENCODING 'UTF8'
  TEMPLATE template0;

\c lambadega_cometa;

-- ============================================================
-- TABELA: usuario (para autenticação e login)
-- ============================================================
CREATE TABLE usuario (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    usuario VARCHAR(50) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    ativo BOOLEAN DEFAULT TRUE,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- TABELA: cliente
-- ============================================================
CREATE TABLE cliente (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    cpf VARCHAR(14) UNIQUE NOT NULL,
    cnpj VARCHAR(18),
    email VARCHAR(100),
    telefone VARCHAR(20),
    celular VARCHAR(20),
    endereco VARCHAR(255),
    numero VARCHAR(10),
    complemento VARCHAR(100),
    bairro VARCHAR(100),
    cidade VARCHAR(100),
    estado VARCHAR(2),
    cep VARCHAR(10),
    status VARCHAR(20) DEFAULT 'ATIVO',
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_cliente_cpf ON cliente(cpf);
CREATE INDEX idx_cliente_nome ON cliente(nome);

-- ============================================================
-- TABELA: motorista
-- ============================================================
CREATE TABLE motorista (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    cpf VARCHAR(14) UNIQUE NOT NULL,
    email VARCHAR(100),
    telefone VARCHAR(20),
    celular VARCHAR(20),
    endereco VARCHAR(255),
    numero VARCHAR(10),
    complemento VARCHAR(100),
    bairro VARCHAR(100),
    cidade VARCHAR(100),
    estado VARCHAR(2),
    cep VARCHAR(10),
    numero_cnh VARCHAR(20) UNIQUE NOT NULL,
    categoria_cnh VARCHAR(5) NOT NULL,
    validade_cnh DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'ATIVO',
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_motorista_cpf ON motorista(cpf);
CREATE INDEX idx_motorista_cnh ON motorista(numero_cnh);
CREATE INDEX idx_motorista_nome ON motorista(nome);
CREATE INDEX idx_motorista_status ON motorista(status);

-- ============================================================
-- TABELA: veiculo
-- ============================================================
CREATE TABLE veiculo (
    id SERIAL PRIMARY KEY,
    placa VARCHAR(10) UNIQUE NOT NULL,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(100) NOT NULL,
    ano INTEGER NOT NULL,
    cor VARCHAR(50),
    tipo VARCHAR(50) NOT NULL,
    capacidade_carga DECIMAL(10, 2),
    numero_chassi VARCHAR(30) UNIQUE,
    numero_motor VARCHAR(30) UNIQUE,
    data_compra DATE,
    status VARCHAR(20) DEFAULT 'DISPONIVEL',
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_veiculo_placa ON veiculo(placa);
CREATE INDEX idx_veiculo_status ON veiculo(status);

-- ============================================================
-- TABELA: frete
-- ============================================================
CREATE TABLE frete (
    id SERIAL PRIMARY KEY,
    numero_frete VARCHAR(50) UNIQUE NOT NULL,
    cliente_id INTEGER NOT NULL,
    motorista_id INTEGER NOT NULL,
    veiculo_id INTEGER NOT NULL,
    endereco_origem VARCHAR(255) NOT NULL,
    endereco_destino VARCHAR(255) NOT NULL,
    data_partida TIMESTAMP,
    data_chegada TIMESTAMP,
    peso_total DECIMAL(10, 2),
    valor_frete DECIMAL(10, 2) NOT NULL,
    valor_desconto DECIMAL(10, 2) DEFAULT 0,
    valor_total DECIMAL(10, 2) NOT NULL,
    observacao TEXT,
    status VARCHAR(20) DEFAULT 'PENDENTE',
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    FOREIGN KEY (motorista_id) REFERENCES motorista(id),
    FOREIGN KEY (veiculo_id) REFERENCES veiculo(id)
);

CREATE INDEX idx_frete_numero ON frete(numero_frete);
CREATE INDEX idx_frete_cliente ON frete(cliente_id);
CREATE INDEX idx_frete_motorista ON frete(motorista_id);
CREATE INDEX idx_frete_veiculo ON frete(veiculo_id);
CREATE INDEX idx_frete_status ON frete(status);
CREATE INDEX idx_frete_data_criacao ON frete(data_criacao);

-- ============================================================
-- DADOS DE EXEMPLO - USUÁRIO ADMIN
-- ============================================================
INSERT INTO usuario (nome, email, usuario, senha, ativo) 
VALUES ('Administrador', 'admin@lambadega.com', 'admin', '123456', TRUE);

-- ============================================================
-- DADOS DE EXEMPLO - CLIENTE
-- ============================================================
INSERT INTO cliente (nome, cpf, email, telefone, endereco, numero, bairro, cidade, estado, cep, status)
VALUES 
('João Silva', '12345678901', 'joao@email.com', '1133334444', 'Rua A', '100', 'Centro', 'São Paulo', 'SP', '01310100', 'ATIVO'),
('Maria Santos', '98765432101', 'maria@email.com', '1144445555', 'Rua B', '200', 'Vila Nova', 'São Paulo', 'SP', '02100000', 'ATIVO');

-- ============================================================
-- DADOS DE EXEMPLO - MOTORISTA
-- ============================================================
INSERT INTO motorista (nome, cpf, email, telefone, numero_cnh, categoria_cnh, validade_cnh, status)
VALUES 
('Carlos Oliveira', '11122233344', 'carlos@email.com', '1188889999', 'CNH123456789', 'D', '2027-12-31', 'ATIVO'),
('Pedro Costa', '55566677788', 'pedro@email.com', '1177776666', 'CNH987654321', 'C', '2026-06-30', 'ATIVO');

-- ============================================================
-- DADOS DE EXEMPLO - VEÍCULO
-- ============================================================
INSERT INTO veiculo (placa, marca, modelo, ano, cor, tipo, capacidade_carga, status)
VALUES 
('ABC1234', 'Volvo', 'FH16', 2020, 'Branco', 'Caminhão', '25000.00', 'DISPONIVEL'),
('XYZ5678', 'Scania', 'R450', 2021, 'Azul', 'Caminhão', '28000.00', 'DISPONIVEL');

-- ============================================================
-- ÍNDICES ADICIONAIS PARA PERFORMANCE
-- ============================================================
CREATE INDEX idx_cliente_status ON cliente(status);

-- ============================================================
-- FIM DO SCRIPT
-- ============================================================
