package br.com.gw.frete;

import br.com.gw.exception.FreteException;
import br.com.gw.exception.NegocioException;
import br.com.gw.motorista.Motorista;
import br.com.gw.motorista.MotoristaDAO;
import br.com.gw.veiculo.Veiculo;
import br.com.gw.veiculo.VeiculoDAO;
import br.com.gw.util.ConnectionFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

public class FreteBO {
    private static final Logger logger = Logger.getLogger(FreteBO.class.getName());

    private final FreteDAO    freteDAO    = new FreteDAO();
    private final MotoristaDAO motoristaDAO = new MotoristaDAO();
    private final VeiculoDAO  veiculoDAO  = new VeiculoDAO();

    public List<Frete> listar(String filtro, int pagina, int limite) throws NegocioException {
        if (pagina < 1) pagina = 1;
        if (limite < 1) limite = 10;
        return freteDAO.listar(filtro, pagina, limite);
    }

    public int contarPaginas(String filtro, int limite) throws NegocioException {
        return (int) Math.ceil((double) freteDAO.contarTotal(filtro) / limite);
    }

    public String exportarCsv(String filtro) throws NegocioException {
        List<Frete> fretes = freteDAO.listarParaExportacao(filtro);
        StringBuilder csv = new StringBuilder();

        csv.append("Numero;Status;Data emissao;Remetente;Destinatario;Motorista;Veiculo;Origem;Destino;")
           .append("Previsao entrega;Data saida;Data entrega;Descricao carga;Peso kg;Volumes;")
           .append("Valor frete;Aliquota ICMS;Valor ICMS;Valor total\n");

        for (Frete frete : fretes) {
            appendLinhaCsv(csv, frete);
        }

        return csv.toString();
    }

    public Frete buscarPorId(int id) throws NegocioException {
        Frete f = freteDAO.buscarPorId(id);
        if (f == null) throw new FreteException("Frete não encontrado.");
        f.setOcorrencias(freteDAO.listarOcorrencias(id));
        return f;
    }

    public String gerarNumeroFrete() throws NegocioException {
        int ano = LocalDate.now().getYear();
        String ultimo = freteDAO.buscarUltimoNumeroDoAno(ano);
        int seq = 1;
        if (ultimo != null) {
            seq = Integer.parseInt(ultimo.split("-")[2]) + 1;
        }
        return String.format("FRT-%d-%05d", ano, seq);
    }

    public void emitir(Frete frete) throws NegocioException {
        validarEmissao(frete);

        frete.setNumero(gerarNumeroFrete());
        frete.setStatus(Frete.Status.EMITIDO);
        frete.setDataEmissao(LocalDateTime.now());

        if (frete.getValorFrete() != null && frete.getAliquotaIcms() != null) {
            BigDecimal icms = frete.getValorFrete()
                .multiply(frete.getAliquotaIcms())
                .divide(BigDecimal.valueOf(100));
            frete.setValorIcms(icms);
            frete.setValorTotal(frete.getValorFrete().add(icms));
        }

        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);
            freteDAO.salvar(frete, conn);
            freteDAO.atualizarStatusVeiculo(frete.getVeiculo().getId(), Veiculo.Status.RESERVADO, conn);
            conn.commit();
            logger.info("Frete emitido: " + frete.getNumero());
        } catch (Exception e) {
            ConnectionFactory.rollback(conn);
            throw new FreteException("Erro ao emitir frete: " + e.getMessage(), e);
        } finally {
            ConnectionFactory.fechar(conn);
        }
    }

    public void confirmarSaida(int idFrete) throws NegocioException {
        Frete frete = buscarPorId(idFrete);

        if (frete.getStatus() != Frete.Status.EMITIDO) {
            throw new FreteException("Somente fretes EMITIDOS podem ter saída confirmada.");
        }

        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            Timestamp agora = new Timestamp(System.currentTimeMillis());
            freteDAO.atualizarStatus(idFrete, Frete.Status.SAIDA_CONFIRMADA, agora, null, conn);
            // Usa enum — não String solta
            freteDAO.atualizarStatusVeiculo(frete.getVeiculo().getId(), Veiculo.Status.EM_VIAGEM, conn);

            OcorrenciaFrete oc = new OcorrenciaFrete();
            oc.setIdFrete(idFrete);
            oc.setTipo(OcorrenciaFrete.Tipo.SAIDA_DO_PATIO);
            oc.setDataHora(LocalDateTime.now());
            oc.setMunicipio(frete.getMunicipioOrigem());
            oc.setUf(frete.getUfOrigem());
            oc.setDescricao("Saída do pátio confirmada.");
            freteDAO.salvarOcorrencia(oc, conn);

            conn.commit();
            logger.info("Saida confirmada: " + frete.getNumero());
        } catch (NegocioException e) {
            ConnectionFactory.rollback(conn); throw e;
        } catch (Exception e) {
            ConnectionFactory.rollback(conn);
            throw new FreteException("Erro ao confirmar saída: " + e.getMessage(), e);
        } finally {
            ConnectionFactory.fechar(conn);
        }
    }

    public void registrarEmTransito(int idFrete, OcorrenciaFrete ocorrencia) throws NegocioException {
        Frete frete = buscarPorId(idFrete);

        if (frete.getStatus() != Frete.Status.SAIDA_CONFIRMADA) {
            throw new FreteException("O frete precisa estar com SAÍDA CONFIRMADA para registrar em trânsito.");
        }
        validarOcorrencia(ocorrencia, frete);

        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            freteDAO.atualizarStatus(idFrete, Frete.Status.EM_TRANSITO,
                new Timestamp(frete.getDataSaida() != null
                    ? Timestamp.valueOf(frete.getDataSaida()).getTime()
                    : System.currentTimeMillis()), null, conn);

            ocorrencia.setIdFrete(idFrete);
            ocorrencia.setTipo(OcorrenciaFrete.Tipo.EM_ROTA);
            freteDAO.salvarOcorrencia(ocorrencia, conn);
            conn.commit();
        } catch (NegocioException e) {
            ConnectionFactory.rollback(conn); throw e;
        } catch (Exception e) {
            ConnectionFactory.rollback(conn);
            throw new FreteException("Erro ao registrar em trânsito: " + e.getMessage(), e);
        } finally {
            ConnectionFactory.fechar(conn);
        }
    }

    public void registrarEntrega(int idFrete, OcorrenciaFrete ocorrencia) throws NegocioException {
        Frete frete = buscarPorId(idFrete);

        if (frete.getStatus() != Frete.Status.EM_TRANSITO) {
            throw new FreteException("Somente fretes EM TRÂNSITO podem ser entregues.");
        }
        if (ocorrencia.getNomeRecebedor() == null || ocorrencia.getNomeRecebedor().trim().isEmpty()) {
            throw new FreteException("O nome do recebedor é obrigatório para registrar entrega.");
        }
        if (ocorrencia.getDocumentoRecebedor() == null || ocorrencia.getDocumentoRecebedor().trim().isEmpty()) {
            throw new FreteException("O documento do recebedor é obrigatório para registrar entrega.");
        }
        validarOcorrencia(ocorrencia, frete);

        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            Timestamp agora   = new Timestamp(System.currentTimeMillis());
            // Preserva data_saida existente
            Timestamp saida   = frete.getDataSaida() != null
                ? Timestamp.valueOf(frete.getDataSaida()) : agora;

            freteDAO.atualizarStatus(idFrete, Frete.Status.ENTREGUE, saida, agora, conn);
            freteDAO.atualizarStatusVeiculo(frete.getVeiculo().getId(), Veiculo.Status.DISPONIVEL, conn);

            ocorrencia.setIdFrete(idFrete);
            ocorrencia.setTipo(OcorrenciaFrete.Tipo.ENTREGA_REALIZADA);
            freteDAO.salvarOcorrencia(ocorrencia, conn);
            conn.commit();
            logger.info("Entrega registrada: " + frete.getNumero());
        } catch (NegocioException e) {
            ConnectionFactory.rollback(conn); throw e;
        } catch (Exception e) {
            ConnectionFactory.rollback(conn);
            throw new FreteException("Erro ao registrar entrega: " + e.getMessage(), e);
        } finally {
            ConnectionFactory.fechar(conn);
        }
    }

    public void registrarNaoEntrega(int idFrete, OcorrenciaFrete ocorrencia) throws NegocioException {
        Frete frete = buscarPorId(idFrete);

        if (frete.getStatus() != Frete.Status.EM_TRANSITO) {
            throw new FreteException("Somente fretes EM TRÂNSITO podem ter não entrega registrada.");
        }
        if (ocorrencia.getDescricao() == null || ocorrencia.getDescricao().trim().isEmpty()) {
            throw new FreteException("O motivo é obrigatório para registrar não entrega.");
        }
        validarOcorrencia(ocorrencia, frete);

        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);

            // Preserva data_saida — não passa null
            Timestamp saida = frete.getDataSaida() != null
                ? Timestamp.valueOf(frete.getDataSaida()) : null;

            freteDAO.atualizarStatus(idFrete, Frete.Status.NAO_ENTREGUE, saida, null, conn);
            freteDAO.atualizarStatusVeiculo(frete.getVeiculo().getId(), Veiculo.Status.DISPONIVEL, conn);

            ocorrencia.setIdFrete(idFrete);
            ocorrencia.setTipo(OcorrenciaFrete.Tipo.TENTATIVA_ENTREGA);
            freteDAO.salvarOcorrencia(ocorrencia, conn);
            conn.commit();
        } catch (NegocioException e) {
            ConnectionFactory.rollback(conn); throw e;
        } catch (Exception e) {
            ConnectionFactory.rollback(conn);
            throw new FreteException("Erro ao registrar não entrega: " + e.getMessage(), e);
        } finally {
            ConnectionFactory.fechar(conn);
        }
    }

    public void cancelar(int idFrete) throws NegocioException {
        Frete frete = buscarPorId(idFrete);

        if (frete.getStatus() == Frete.Status.SAIDA_CONFIRMADA
                || frete.getStatus() == Frete.Status.EM_TRANSITO
                || frete.getStatus() == Frete.Status.ENTREGUE
                || frete.getStatus() == Frete.Status.NAO_ENTREGUE
                || frete.getStatus() == Frete.Status.CANCELADO) {
            throw new FreteException("Não é possível cancelar um frete com status: " + frete.getStatus());
        }

        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);
            freteDAO.atualizarStatus(idFrete, Frete.Status.CANCELADO, null, null, conn);
            freteDAO.atualizarStatusVeiculo(frete.getVeiculo().getId(), Veiculo.Status.DISPONIVEL, conn);
            conn.commit();
            logger.info("Frete cancelado: " + frete.getNumero());
        } catch (NegocioException e) {
            ConnectionFactory.rollback(conn); throw e;
        } catch (Exception e) {
            ConnectionFactory.rollback(conn);
            throw new FreteException("Erro ao cancelar frete: " + e.getMessage(), e);
        } finally {
            ConnectionFactory.fechar(conn);
        }
    }

    public void registrarOcorrencia(int idFrete, OcorrenciaFrete ocorrencia) throws NegocioException {
        Frete frete = buscarPorId(idFrete);

        if (!frete.podeReceberOcorrencia()) {
            throw new FreteException("Não é permitido registrar ocorrência em frete com status: "
                + frete.getStatus());
        }
        if (ocorrencia.getTipo() == OcorrenciaFrete.Tipo.ENTREGA_REALIZADA) {
            registrarEntrega(idFrete, ocorrencia);
            return;
        }
        if (ocorrencia.getTipo() == OcorrenciaFrete.Tipo.TENTATIVA_ENTREGA) {
            registrarNaoEntrega(idFrete, ocorrencia);
            return;
        }
        validarOcorrencia(ocorrencia, frete);

        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);
            ocorrencia.setIdFrete(idFrete);
            freteDAO.salvarOcorrencia(ocorrencia, conn);
            conn.commit();
        } catch (NegocioException e) {
            ConnectionFactory.rollback(conn); throw e;
        } catch (Exception e) {
            ConnectionFactory.rollback(conn);
            throw new FreteException("Erro ao registrar ocorrência: " + e.getMessage(), e);
        } finally {
            ConnectionFactory.fechar(conn);
        }
    }

    private void validarEmissao(Frete f) throws NegocioException {
        if (f.getRemetente() == null || f.getRemetente().getId() <= 0)
            throw new FreteException("Remetente é obrigatório.");
        if (f.getDestinatario() == null || f.getDestinatario().getId() <= 0)
            throw new FreteException("Destinatário é obrigatório.");
        if (f.getMotorista() == null || f.getMotorista().getId() <= 0)
            throw new FreteException("Motorista é obrigatório.");
        if (f.getVeiculo() == null || f.getVeiculo().getId() <= 0)
            throw new FreteException("Veículo é obrigatório.");
        if (f.getMunicipioOrigem() == null || f.getMunicipioOrigem().trim().isEmpty())
            throw new FreteException("Município de origem é obrigatório.");
        if (f.getUfOrigem() == null || f.getUfOrigem().trim().isEmpty())
            throw new FreteException("UF de origem é obrigatória.");
        if (f.getMunicipioDestino() == null || f.getMunicipioDestino().trim().isEmpty())
            throw new FreteException("Município de destino é obrigatório.");
        if (f.getUfDestino() == null || f.getUfDestino().trim().isEmpty())
            throw new FreteException("UF de destino é obrigatória.");
        if (f.getDataPrevisaoEntrega() == null)
            throw new FreteException("Data prevista de entrega é obrigatória.");
        if (!f.getDataPrevisaoEntrega().isAfter(LocalDate.now()))
            throw new FreteException("A data prevista de entrega deve ser posterior à data de hoje.");

        Veiculo veiculo = veiculoDAO.buscarPorId(f.getVeiculo().getId());
        if (veiculo.getStatus() != Veiculo.Status.DISPONIVEL)
            throw new FreteException("O veículo não está disponível (status: " + veiculo.getStatus() + ").");

        if (f.getPesoKg() != null && f.getPesoKg().doubleValue() > veiculo.getCapacidadeKg())
            throw new FreteException("Peso da carga excede a capacidade do veículo ("
                + veiculo.getCapacidadeKg() + " kg).");

        Motorista motorista = motoristaDAO.buscarPorId(f.getMotorista().getId());
        if (motorista.getStatus() != Motorista.Status.ATIVO)
            throw new FreteException("O motorista não está ativo.");

        // CNH válida na data de emissão do frete — não apenas hoje
        LocalDate dataEmissao = LocalDate.now();
        if (motorista.getCnhValidade() != null && motorista.getCnhValidade().isBefore(dataEmissao))
            throw new FreteException("A CNH do motorista está vencida.");

        // Motorista não pode ter frete em SAIDA_CONFIRMADA ou EM_TRANSITO
        if (motoristaDAO.possuiFretesAtivos(motorista.getId()))
            throw new FreteException("O motorista já possui frete em andamento (SAÍDA CONFIRMADA ou EM TRÂNSITO).");
    }

    private void validarOcorrencia(OcorrenciaFrete oc, Frete frete) throws NegocioException {
        if (oc.getDataHora() == null)
            throw new FreteException("Data/hora da ocorrência é obrigatória.");
        if (oc.getMunicipio() == null || oc.getMunicipio().trim().isEmpty())
            throw new FreteException("Município da ocorrência é obrigatório.");
        if (oc.getUf() == null || oc.getUf().trim().isEmpty())
            throw new FreteException("UF da ocorrência é obrigatória.");

        LocalDateTime ultima = freteDAO.buscarDataHoraUltimaOcorrencia(frete.getId());
        if (ultima != null && !oc.getDataHora().isAfter(ultima))
            throw new FreteException("A data/hora deve ser posterior à última ocorrência registrada.");

        if (oc.getTipo() == OcorrenciaFrete.Tipo.AVARIA
                || oc.getTipo() == OcorrenciaFrete.Tipo.EXTRAVIO
                || oc.getTipo() == OcorrenciaFrete.Tipo.OUTROS) {
            if (oc.getDescricao() == null || oc.getDescricao().trim().isEmpty())
                throw new FreteException("Descrição é obrigatória para o tipo: " + oc.getTipo());
        }
    }

    private void appendLinhaCsv(StringBuilder csv, Frete frete) {
        appendCampo(csv, frete.getNumero());
        appendCampo(csv, frete.getStatus());
        appendCampo(csv, frete.getDataEmissao());
        appendCampo(csv, frete.getRemetente() != null ? frete.getRemetente().getNomeRazaoSocial() : null);
        appendCampo(csv, frete.getDestinatario() != null ? frete.getDestinatario().getNomeRazaoSocial() : null);
        appendCampo(csv, frete.getMotorista() != null ? frete.getMotorista().getNome() : null);
        appendCampo(csv, frete.getVeiculo() != null ? frete.getVeiculo().getPlaca() : null);
        appendCampo(csv, montarLocalidade(frete.getMunicipioOrigem(), frete.getUfOrigem()));
        appendCampo(csv, montarLocalidade(frete.getMunicipioDestino(), frete.getUfDestino()));
        appendCampo(csv, frete.getDataPrevisaoEntrega());
        appendCampo(csv, frete.getDataSaida());
        appendCampo(csv, frete.getDataEntrega());
        appendCampo(csv, frete.getDescricaoCarga());
        appendCampo(csv, frete.getPesoKg());
        appendCampo(csv, frete.getVolumes());
        appendCampo(csv, frete.getValorFrete());
        appendCampo(csv, frete.getAliquotaIcms());
        appendCampo(csv, frete.getValorIcms());
        appendUltimoCampo(csv, frete.getValorTotal());
    }

    private void appendCampo(StringBuilder csv, Object valor) {
        appendValor(csv, valor);
        csv.append(';');
    }

    private void appendUltimoCampo(StringBuilder csv, Object valor) {
        appendValor(csv, valor);
        csv.append('\n');
    }

    private void appendValor(StringBuilder csv, Object valor) {
        if (valor == null) return;

        String texto = String.valueOf(valor).replace("\"", "\"\"");
        if (texto.contains(";") || texto.contains("\"") || texto.contains("\n") || texto.contains("\r")) {
            csv.append('"').append(texto).append('"');
        } else {
            csv.append(texto);
        }
    }

    private String montarLocalidade(String municipio, String uf) {
        if (municipio == null || municipio.trim().isEmpty()) return uf;
        if (uf == null || uf.trim().isEmpty()) return municipio;
        return municipio + "/" + uf;
    }
}
