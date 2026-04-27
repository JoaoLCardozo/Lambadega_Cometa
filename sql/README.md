-- README - INSTRUÇÕES PARA EXECUTAR OS SCRIPTS SQL (PostgreSQL)
-- Data: 23 de abril de 2026

/*
ORDEM DE EXECUÇÃO DOS SCRIPTS:

1. LIMPEZA (OPCIONAL):
   - Se deseja resetar o banco, execute: 00_drop_database.sql
   
2. CRIAÇÃO:
   - Execute: 01_create_database.sql
   - Este script criará o banco e todas as tabelas com dados de exemplo

3. MANUTENÇÃO/MELHORIAS:
   - Execute: 02_views_and_maintenance.sql
   - Este script cria views úteis e pode ser usado para alterações futuras

TABELAS CRIADAS:
- usuario (autenticação)
- cliente (dados dos clientes)
- motorista (dados dos motoristas)
- veiculo (dados dos veículos)
- frete (informações de fretes)

DADOS DE EXEMPLO:
- Usuario admin: admin / 123456
- 2 clientes de exemplo
- 2 motoristas de exemplo
- 2 veículos de exemplo

CONEXÃO AO BANCO (PostgreSQL):
- Host: localhost
- Porta: 5432
- Database: lambadega_cometa
- Usuario: postgres
- Senha: 1234

COMO EXECUTAR OS SCRIPTS NO PostgreSQL:

Via psql (linha de comando):
  psql -U postgres -h localhost -d postgres -f 01_create_database.sql
  psql -U postgres -h localhost -d lambadega_cometa -f 02_views_and_maintenance.sql

Via DBeaver ou pgAdmin:
  1. Abra a ferramenta
  2. Conecte ao PostgreSQL
  3. Abra cada arquivo SQL
  4. Execute (F5 ou Ctrl+Enter)

IMPORTANTE:
- A senha do usuário admin é simples (apenas para testes)
- Em produção, use senhas fortes e criptografadas
- Adapte os dados de exemplo conforme necessário
- Use SERIAL para auto-increment no PostgreSQL (não INT AUTO_INCREMENT)
- Use TIMESTAMP ao invés de DATETIME no PostgreSQL
*/
