-- Dados extras para demonstrar o Monitor de Fretes.
-- Execute depois do 01_create_database.sql.

\c LambadegaCometa;

BEGIN;

-- Clientes usados como remetentes/destinatários nos cenários do monitor de fretes.
INSERT INTO cliente (tipo_pessoa, nome_razao_social, nome_fantasia, documento, inscricao_estadual,
    logradouro, numero, bairro, municipio, uf, cep, telefone, email, status) VALUES
('J', 'Comercial Aurora Recife Ltda.', 'Aurora Recife', '04252011000110', '444555666',
    'Rua da Aurora', '450', 'Boa Vista', 'Recife', 'PE', '50050000', '8132111000', 'operacao@aurorarecife.com.br', 'ATIVO'),
('J', 'Mercado Sertão Distribuição S.A.', 'Sertão Distribuição', '27865757000102', '777888999',
    'Av. Agamenon Magalhães', '920', 'Maurício de Nassau', 'Caruaru', 'PE', '55012000', '8132112000', 'logistica@sertaodistribuicao.com.br', 'ATIVO'),
('J', 'Hospital Atlântico Norte', 'Hospital Atlântico', '19131243000197', 'ISENTO',
    'Av. Conselheiro Aguiar', '1800', 'Boa Viagem', 'Recife', 'PE', '51020020', '8132113000', 'compras@hospitalatlantico.com.br', 'ATIVO'),
('F', 'Marina Cavalcanti Gomes', NULL, '52998224725', NULL,
    'Rua do Sol', '88', 'Centro', 'Olinda', 'PE', '53020010', '81988881111', 'marina.gomes@email.com', 'ATIVO'),
('J', 'Cliente Inativo Demonstração Ltda.', 'Cliente Inativo', '11244477000161', '333222111',
    'Rua Sem Operação', '12', 'Centro', 'Recife', 'PE', '50030000', '8132114000', 'inativo@demo.com.br', 'INATIVO')
ON CONFLICT (documento) DO NOTHING;

-- Motoristas: inclui um motorista ativo com CNH vencida para gerar alerta.
INSERT INTO motorista (nome, cpf, data_nascimento, telefone, cnh_numero,
    cnh_categoria, cnh_validade, tipo_vinculo, status) VALUES
('Ana Paula Barreto', '52998224725', '1988-06-20', '81977770101',
    'CNH-MON-001', 'D', CURRENT_DATE + INTERVAL '18 months', 'FUNCIONARIO', 'ATIVO'),
('Rafael Moura Lima', '15350946056', '1982-02-12', '81977770202',
    'CNH-MON-002', 'E', CURRENT_DATE + INTERVAL '10 months', 'AGREGADO', 'ATIVO'),
('Bruno Henrique Torres', '98765432100', '1991-09-03', '81977770303',
    'CNH-MON-003', 'C', CURRENT_DATE + INTERVAL '2 years', 'TERCEIRO', 'ATIVO'),
('Luciana Freitas Costa', '39053344705', '1985-12-11', '81977770404',
    'CNH-MON-004', 'D', CURRENT_DATE - INTERVAL '12 days', 'FUNCIONARIO', 'ATIVO'),
('Fábio Nascimento Alves', '11144477735', '1978-04-18', '81977770505',
    'CNH-MON-005', 'E', CURRENT_DATE + INTERVAL '8 months', 'FUNCIONARIO', 'INATIVO')
ON CONFLICT (cpf) DO NOTHING;

-- Veículos livres para gerar fretes ativos e manter capacidade visível no monitor de fretes.
INSERT INTO veiculo (placa, rntrc, ano_fabricacao, tipo, tara_kg, capacidade_kg, volume_m3, status) VALUES
('MON1A01', 'RNTRC-MON-001', 2023, 'TRUCK', 6200, 15000, 48.0, 'DISPONIVEL'),
('MON1A02', 'RNTRC-MON-002', 2021, 'CARRETA', 8100, 28000, 92.0, 'DISPONIVEL'),
('MON1A03', 'RNTRC-MON-003', 2022, 'VAN', 2100, 3500, 14.0, 'DISPONIVEL'),
('MON1A04', 'RNTRC-MON-004', 2020, 'TRUCK', 5900, 13500, 43.0, 'DISPONIVEL'),
('MON1A05', 'RNTRC-MON-005', 2024, 'UTILITARIO', 1800, 2200, 9.0, 'DISPONIVEL'),
('MON1A06', 'RNTRC-MON-006', 2019, 'CARRETA', 7900, 26000, 88.0, 'DISPONIVEL'),
('MON1A07', 'RNTRC-MON-007', 2022, 'TRUCK', 6100, 14500, 46.0, 'DISPONIVEL'),
('MON1A08', 'RNTRC-MON-008', 2023, 'VAN', 2000, 3200, 13.0, 'DISPONIVEL'),
('MON1A09', 'RNTRC-MON-009', 2021, 'CARRETA', 8300, 30000, 95.0, 'DISPONIVEL'),
('MON1B10', 'RNTRC-MON-010', 2020, 'TRUCK', 6000, 14000, 44.0, 'DISPONIVEL'),
('MON1B11', 'RNTRC-MON-011', 2022, 'TRUCK', 6150, 14500, 46.0, 'DISPONIVEL'),
('MON1B12', 'RNTRC-MON-012', 2023, 'VAN', 1950, 3100, 12.5, 'DISPONIVEL'),
('MON1B13', 'RNTRC-MON-013', 2021, 'UTILITARIO', 1750, 2100, 8.5, 'DISPONIVEL')
ON CONFLICT (placa) DO NOTHING;

-- Fretes críticos: atrasados e vencendo hoje.
INSERT INTO frete (numero, id_remetente, id_destinatario, id_motorista, id_veiculo,
    municipio_origem, uf_origem, municipio_destino, uf_destino, descricao_carga,
    peso_kg, volumes, valor_frete, aliquota_icms, valor_icms, valor_total,
    status, data_emissao, data_previsao_entrega, data_saida) VALUES
('FRT-MON-001',
    (SELECT id FROM cliente WHERE documento = '04252011000110'),
    (SELECT id FROM cliente WHERE documento = '27865757000102'),
    (SELECT id FROM motorista WHERE cpf = '52998224725'),
    (SELECT id FROM veiculo WHERE placa = 'MON1A01'),
    'Recife', 'PE', 'Caruaru', 'PE', 'Medicamentos refrigerados',
    4200.00, 18, 2400.00, 12.00, 288.00, 2688.00,
    'EM_TRANSITO', NOW() - INTERVAL '4 days', CURRENT_DATE - 2, NOW() - INTERVAL '3 days'),
('FRT-MON-002',
    (SELECT id FROM cliente WHERE documento = '27865757000102'),
    (SELECT id FROM cliente WHERE documento = '19131243000197'),
    (SELECT id FROM motorista WHERE cpf = '15350946056'),
    (SELECT id FROM veiculo WHERE placa = 'MON1A02'),
    'Caruaru', 'PE', 'Recife', 'PE', 'Equipamentos hospitalares',
    9800.00, 12, 3600.00, 12.00, 432.00, 4032.00,
    'SAIDA_CONFIRMADA', NOW() - INTERVAL '3 days', CURRENT_DATE - 1, NOW() - INTERVAL '18 hours'),
('FRT-MON-003',
    (SELECT id FROM cliente WHERE documento = '19131243000197'),
    (SELECT id FROM cliente WHERE documento = '52998224725'),
    (SELECT id FROM motorista WHERE cpf = '98765432100'),
    (SELECT id FROM veiculo WHERE placa = 'MON1A03'),
    'Recife', 'PE', 'Olinda', 'PE', 'Documentos e suprimentos urgentes',
    900.00, 6, 850.00, 12.00, 102.00, 952.00,
    'EMITIDO', NOW() - INTERVAL '2 days', CURRENT_DATE - 1, NULL),
('FRT-MON-004',
    (SELECT id FROM cliente WHERE documento = '04252011000110'),
    (SELECT id FROM cliente WHERE documento = '19131243000197'),
    (SELECT id FROM motorista WHERE cpf = '52998224725'),
    (SELECT id FROM veiculo WHERE placa = 'MON1A04'),
    'Recife', 'PE', 'Recife', 'PE', 'Peças de reposição',
    5200.00, 10, 1900.00, 12.00, 228.00, 2128.00,
    'EM_TRANSITO', NOW() - INTERVAL '1 day', CURRENT_DATE, NOW() - INTERVAL '6 hours'),
('FRT-MON-005',
    (SELECT id FROM cliente WHERE documento = '27865757000102'),
    (SELECT id FROM cliente WHERE documento = '04252011000110'),
    (SELECT id FROM motorista WHERE cpf = '15350946056'),
    (SELECT id FROM veiculo WHERE placa = 'MON1A05'),
    'Caruaru', 'PE', 'Recife', 'PE', 'Reposição de estoque',
    1600.00, 9, 1100.00, 12.00, 132.00, 1232.00,
    'EMITIDO', NOW() - INTERVAL '6 hours', CURRENT_DATE, NULL),
('FRT-MON-006',
    (SELECT id FROM cliente WHERE documento = '19131243000197'),
    (SELECT id FROM cliente WHERE documento = '27865757000102'),
    (SELECT id FROM motorista WHERE cpf = '98765432100'),
    (SELECT id FROM veiculo WHERE placa = 'MON1A06'),
    'Recife', 'PE', 'Caruaru', 'PE', 'Carga programada futura',
    12400.00, 22, 4100.00, 12.00, 492.00, 4592.00,
    'SAIDA_CONFIRMADA', NOW() - INTERVAL '8 hours', CURRENT_DATE + 3, NOW() - INTERVAL '2 hours')
ON CONFLICT (numero) DO NOTHING;

-- Entregas concluídas no mês atual para alimentar o ranking de motoristas.
INSERT INTO frete (numero, id_remetente, id_destinatario, id_motorista, id_veiculo,
    municipio_origem, uf_origem, municipio_destino, uf_destino, descricao_carga,
    peso_kg, volumes, valor_frete, aliquota_icms, valor_icms, valor_total,
    status, data_emissao, data_previsao_entrega, data_saida, data_entrega) VALUES
('FRT-MON-101',
    (SELECT id FROM cliente WHERE documento = '04252011000110'),
    (SELECT id FROM cliente WHERE documento = '52998224725'),
    (SELECT id FROM motorista WHERE cpf = '52998224725'),
    (SELECT id FROM veiculo WHERE placa = 'MON1A07'),
    'Recife', 'PE', 'Olinda', 'PE', 'Entrega concluída expressa',
    1200.00, 4, 900.00, 12.00, 108.00, 1008.00,
    'ENTREGUE', NOW() - INTERVAL '8 days', CURRENT_DATE - 6, NOW() - INTERVAL '7 days', NOW() - INTERVAL '6 days'),
('FRT-MON-102',
    (SELECT id FROM cliente WHERE documento = '27865757000102'),
    (SELECT id FROM cliente WHERE documento = '04252011000110'),
    (SELECT id FROM motorista WHERE cpf = '52998224725'),
    (SELECT id FROM veiculo WHERE placa = 'MON1A08'),
    'Caruaru', 'PE', 'Recife', 'PE', 'Carga leve concluída',
    700.00, 3, 780.00, 12.00, 93.60, 873.60,
    'ENTREGUE', NOW() - INTERVAL '6 days', CURRENT_DATE - 4, NOW() - INTERVAL '5 days', NOW() - INTERVAL '4 days'),
('FRT-MON-103',
    (SELECT id FROM cliente WHERE documento = '19131243000197'),
    (SELECT id FROM cliente WHERE documento = '27865757000102'),
    (SELECT id FROM motorista WHERE cpf = '52998224725'),
    (SELECT id FROM veiculo WHERE placa = 'MON1A09'),
    'Recife', 'PE', 'Caruaru', 'PE', 'Entrega de alto valor concluída',
    6200.00, 14, 3100.00, 12.00, 372.00, 3472.00,
    'ENTREGUE', NOW() - INTERVAL '4 days', CURRENT_DATE - 2, NOW() - INTERVAL '3 days', NOW() - INTERVAL '2 days'),
('FRT-MON-104',
    (SELECT id FROM cliente WHERE documento = '27865757000102'),
    (SELECT id FROM cliente WHERE documento = '19131243000197'),
    (SELECT id FROM motorista WHERE cpf = '15350946056'),
    (SELECT id FROM veiculo WHERE placa = 'MON1B10'),
    'Caruaru', 'PE', 'Recife', 'PE', 'Entrega hospitalar concluída',
    5400.00, 16, 2600.00, 12.00, 312.00, 2912.00,
    'ENTREGUE', NOW() - INTERVAL '5 days', CURRENT_DATE - 3, NOW() - INTERVAL '4 days', NOW() - INTERVAL '3 days'),
('FRT-MON-105',
    (SELECT id FROM cliente WHERE documento = '04252011000110'),
    (SELECT id FROM cliente WHERE documento = '19131243000197'),
    (SELECT id FROM motorista WHERE cpf = '15350946056'),
    (SELECT id FROM veiculo WHERE placa = 'MON1B11'),
    'Recife', 'PE', 'Recife', 'PE', 'Entrega metropolitana concluída',
    1800.00, 7, 1200.00, 12.00, 144.00, 1344.00,
    'ENTREGUE', NOW() - INTERVAL '3 days', CURRENT_DATE - 1, NOW() - INTERVAL '2 days', NOW() - INTERVAL '1 day'),
('FRT-MON-106',
    (SELECT id FROM cliente WHERE documento = '52998224725'),
    (SELECT id FROM cliente WHERE documento = '04252011000110'),
    (SELECT id FROM motorista WHERE cpf = '98765432100'),
    (SELECT id FROM veiculo WHERE placa = 'MON1B12'),
    'Olinda', 'PE', 'Recife', 'PE', 'Coleta e entrega concluída',
    900.00, 2, 620.00, 12.00, 74.40, 694.40,
    'ENTREGUE', NOW() - INTERVAL '2 days', CURRENT_DATE - 1, NOW() - INTERVAL '2 days', NOW() - INTERVAL '1 day')
ON CONFLICT (numero) DO NOTHING;

-- Um cancelamento extra para variar a distribuição de status.
INSERT INTO frete (numero, id_remetente, id_destinatario, id_motorista, id_veiculo,
    municipio_origem, uf_origem, municipio_destino, uf_destino, descricao_carga,
    peso_kg, volumes, valor_frete, aliquota_icms, valor_icms, valor_total,
    status, data_emissao, data_previsao_entrega) VALUES
('FRT-MON-201',
    (SELECT id FROM cliente WHERE documento = '19131243000197'),
    (SELECT id FROM cliente WHERE documento = '04252011000110'),
    (SELECT id FROM motorista WHERE cpf = '15350946056'),
    (SELECT id FROM veiculo WHERE placa = 'MON1B13'),
    'Recife', 'PE', 'Recife', 'PE', 'Frete cancelado para demonstrativo',
    2200.00, 5, 980.00, 12.00, 117.60, 1097.60,
    'CANCELADO', NOW() - INTERVAL '1 day', CURRENT_DATE + 4)
ON CONFLICT (numero) DO NOTHING;

-- Ocorrências para um dos fretes críticos.
INSERT INTO ocorrencia_frete (id_frete, tipo, data_hora, municipio, uf, descricao)
SELECT f.id, 'SAIDA_DO_PATIO', NOW() - INTERVAL '3 days', 'Recife', 'PE', 'Saída registrada para carga crítica'
FROM frete f
WHERE f.numero = 'FRT-MON-001'
  AND NOT EXISTS (
      SELECT 1 FROM ocorrencia_frete o
      WHERE o.id_frete = f.id AND o.tipo = 'SAIDA_DO_PATIO'
  );

INSERT INTO ocorrencia_frete (id_frete, tipo, data_hora, municipio, uf, descricao)
SELECT f.id, 'EM_ROTA', NOW() - INTERVAL '2 days', 'Gravatá', 'PE', 'Veículo em rota com atraso monitorado'
FROM frete f
WHERE f.numero = 'FRT-MON-001'
  AND NOT EXISTS (
      SELECT 1 FROM ocorrencia_frete o
      WHERE o.id_frete = f.id AND o.tipo = 'EM_ROTA'
  );

COMMIT;
