-- Script de alterações e manutenção do banco de dados - PostgreSQL
-- Data: 23 de abril de 2026

-- Criar view para relatório de fretes por motorista
DROP VIEW IF EXISTS vw_fretes_por_motorista CASCADE;
CREATE VIEW vw_fretes_por_motorista AS
SELECT 
    m.nome AS motorista,
    COUNT(f.id) AS total_fretes,
    COALESCE(SUM(f.valor_frete), 0) AS valor_total,
    f.status
FROM frete f
JOIN motorista m ON f.motorista_id = m.id
GROUP BY m.id, m.nome, f.status;

-- Criar view para relatório de clientes ativos
DROP VIEW IF EXISTS vw_clientes_ativos CASCADE;
CREATE VIEW vw_clientes_ativos AS
SELECT 
    c.id,
    c.nome,
    c.cpf,
    c.email,
    COUNT(f.id) AS total_fretes,
    MAX(f.data_criacao) AS ultimo_frete
FROM cliente c
LEFT JOIN frete f ON c.id = f.cliente_id
WHERE c.status = 'ATIVO'
GROUP BY c.id, c.nome, c.cpf, c.email;

-- Criar view para veículos em uso
DROP VIEW IF EXISTS vw_veiculos_em_uso CASCADE;
CREATE VIEW vw_veiculos_em_uso AS
SELECT 
    v.id,
    v.placa,
    v.marca,
    v.modelo,
    v.status,
    COUNT(f.id) AS fretes_realizados,
    MAX(f.data_criacao) AS ultimo_frete
FROM veiculo v
LEFT JOIN frete f ON v.id = f.veiculo_id
GROUP BY v.id, v.placa, v.marca, v.modelo, v.status;

-- FIM DO SCRIPT
