-- Script de drop do banco de dados (para limpeza/reset) - PostgreSQL
-- Data: 23 de abril de 2026

-- Forçar encerramento de conexões ativas
SELECT pg_terminate_backend(pg_stat_activity.pid)
FROM pg_stat_activity
WHERE pg_stat_activity.datname = 'lambadega_cometa'
  AND pid <> pg_backend_pid();

-- Remover o banco
DROP DATABASE IF EXISTS lambadega_cometa;
