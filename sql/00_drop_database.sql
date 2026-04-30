-- Script de drop do banco de dados (para limpeza/reset) - PostgreSQL

-- Remover o banco
DROP DATABASE IF EXISTS LambadegaCometa;

-- Limpar tabelas se existirem (ordem inversa de dependência)
DROP TABLE IF EXISTS ocorrencia_frete CASCADE;
DROP TABLE IF EXISTS frete CASCADE;
DROP TABLE IF EXISTS veiculo CASCADE;
DROP TABLE IF EXISTS motorista CASCADE;
DROP TABLE IF EXISTS cliente CASCADE;
DROP TABLE IF EXISTS usuario CASCADE;
