package br.com.gw.frete;

import br.com.gw.cliente.Cliente;
import br.com.gw.cliente.ClienteBO;
import br.com.gw.exception.NegocioException;
import br.com.gw.motorista.Motorista;
import br.com.gw.motorista.MotoristaBO;
import br.com.gw.veiculo.Veiculo;
import br.com.gw.veiculo.VeiculoBO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@WebServlet("/FreteControlador")
public class FreteControlador extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(FreteControlador.class.getName());
    private static final int LIMITE = 10;

    // Apenas BOs — sem DAOs diretamente
    private final FreteBO    freteBO    = new FreteBO();
    private final ClienteBO  clienteBO  = new ClienteBO();
    private final MotoristaBO motoristaBO = new MotoristaBO();
    private final VeiculoBO  veiculoBO  = new VeiculoBO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String acao = req.getParameter("acao");
        if (acao == null) acao = "listar";
        try {
            switch (acao) {
                case "listar":         listar(req, resp);        break;
                case "exportarCsv":    exportarCsv(req, resp);   break;
                case "relatorioFretesAbertos":
                    relatorioFretesAbertos(req, resp);
                    break;
                case "romaneio":       romaneio(req, resp);      break;
                case "performanceMotorista":
                    performanceMotorista(req, resp);
                    break;
                case "relatorioPerformanceMotorista":
                    relatorioPerformanceMotorista(req, resp);
                    break;
                case "novo":           novo(req, resp);           break;
                case "detalhe":        detalhe(req, resp);        break;
                case "confirmarSaida": confirmarSaida(req, resp); break;
                case "cancelar":       cancelar(req, resp);       break;
                case "novaOcorrencia": novaOcorrencia(req, resp); break;
                default:               listar(req, resp);
            }
        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            try { listar(req, resp); } catch (NegocioException ex) {
                logger.severe("Erro ao recarregar listagem: " + ex.getMessage());
                resp.sendRedirect(req.getContextPath() + "/erro.jsp");
            }
        } catch (Exception e) {
            logger.severe("Erro inesperado FreteControlador GET: " + e.getMessage());
            System.err.println("[FreteControlador] " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/erro.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String acao = req.getParameter("acao");
        if (acao == null) acao = "";
        try {
            switch (acao) {
                case "emitir":           emitir(req, resp);           break;
                case "emTransito":       emTransito(req, resp);       break;
                case "registrarEntrega": registrarEntrega(req, resp); break;
                case "naoEntregue":      naoEntregue(req, resp);      break;
                case "ocorrencia":       ocorrencia(req, resp);       break;
                default:
                    resp.sendRedirect(req.getContextPath() + "/FreteControlador?acao=listar");
            }
        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            String idFrete = req.getParameter("idFrete");
            if (idFrete != null) {
                try {
                    req.setAttribute("frete", freteBO.buscarPorId(Integer.parseInt(idFrete)));
                } catch (Exception ex) { /* ignora */ }
            }
            req.getRequestDispatcher("/WEB-INF/views/frete/detalharFrete.jsp").forward(req, resp);
        } catch (Exception e) {
            logger.severe("Erro inesperado FreteControlador POST: " + e.getMessage());
            System.err.println("[FreteControlador] " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/erro.jsp");
        }
    }

    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException, NegocioException {
        String filtro   = req.getParameter("filtro");
        int pagina      = parsePagina(req.getParameter("pagina"));
        List<Frete> lista     = freteBO.listar(filtro, pagina, LIMITE);
        int totalPaginas      = freteBO.contarPaginas(filtro, LIMITE);
        req.setAttribute("listaFretes",  lista);
        req.setAttribute("filtro",       filtro);
        req.setAttribute("paginaAtual",  pagina);
        req.setAttribute("totalPaginas", totalPaginas);
        req.getRequestDispatcher("/WEB-INF/views/frete/listarFrete.jsp").forward(req, resp);
    }

    private void exportarCsv(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, NegocioException {
        String filtro = req.getParameter("filtro");
        String csv = freteBO.exportarCsv(filtro);
        String nomeArquivo = "fretes-" + LocalDate.now() + ".csv";

        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/csv; charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + nomeArquivo + "\"");
        resp.getWriter().write('\uFEFF');
        resp.getWriter().write(csv);
    }

    private void relatorioFretesAbertos(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, NegocioException {
        enviarPdf(resp, freteBO.gerarRelatorioFretesAbertos(),
            "fretes-em-aberto-" + LocalDate.now() + ".pdf");
    }

    private void romaneio(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, NegocioException {
        int id = Integer.parseInt(req.getParameter("id"));
        Frete frete = freteBO.buscarPorId(id);
        enviarPdf(resp, freteBO.gerarRomaneio(id),
            "romaneio-" + frete.getNumero() + ".pdf");
    }

    private void performanceMotorista(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException, NegocioException {
        req.setAttribute("listaMotoristas", motoristaBO.listar(null, 1, 999));
        req.getRequestDispatcher("/WEB-INF/views/frete/performanceMotorista.jsp").forward(req, resp);
    }

    private void relatorioPerformanceMotorista(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, NegocioException {
        int idMotorista = parseIdMotorista(req.getParameter("idMotorista"));
        LocalDate dataInicio = parseDataObrigatoria(req.getParameter("dataInicio"), "Data inicial");
        LocalDate dataFim = parseDataObrigatoria(req.getParameter("dataFim"), "Data final");
        Motorista motorista = motoristaBO.buscarPorId(idMotorista);

        enviarPdf(resp, freteBO.gerarPerformanceMotorista(idMotorista, dataInicio, dataFim),
            "performance-motorista-" + motorista.getId() + "-" + LocalDate.now() + ".pdf");
    }

    private void enviarPdf(HttpServletResponse resp, byte[] pdf, String nomeArquivo)
            throws IOException {
        resp.setContentType("application/pdf");
        resp.setHeader("Content-Disposition", "inline; filename=\"" + nomeArquivo + "\"");
        resp.setContentLength(pdf.length);
        resp.getOutputStream().write(pdf);
    }

    private void novo(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException, NegocioException {
        // Controller chama BO — não DAO
        req.setAttribute("listaClientes",   clienteBO.listar(null, null, null, null, "ATIVO", 1, 999));
        req.setAttribute("listaMotoristas", motoristaBO.listar(null, 1, 999));
        req.setAttribute("listaVeiculos",   veiculoBO.listar(null, 1, 999));
        req.setAttribute("frete", new Frete());
        req.getRequestDispatcher("/WEB-INF/views/frete/formFrete.jsp").forward(req, resp);
    }

    private void detalhe(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException, NegocioException {
        int id = Integer.parseInt(req.getParameter("id"));
        req.setAttribute("frete", freteBO.buscarPorId(id));
        req.getRequestDispatcher("/WEB-INF/views/frete/detalharFrete.jsp").forward(req, resp);
    }

    private void confirmarSaida(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, NegocioException {
        int id = Integer.parseInt(req.getParameter("id"));
        freteBO.confirmarSaida(id);
        resp.sendRedirect(req.getContextPath() + "/FreteControlador?acao=detalhe&id=" + id);
    }

    private void cancelar(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, NegocioException {
        int id = Integer.parseInt(req.getParameter("id"));
        freteBO.cancelar(id);
        resp.sendRedirect(req.getContextPath() + "/FreteControlador?acao=listar");
    }

    private void novaOcorrencia(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException, NegocioException {
        int id = Integer.parseInt(req.getParameter("id"));
        req.setAttribute("frete", freteBO.buscarPorId(id));
        req.getRequestDispatcher("/WEB-INF/views/frete/ocorrenciaFrete.jsp").forward(req, resp);
    }

    private void emitir(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException, NegocioException {
        Frete f = montarFreteDaRequisicao(req);
        try {
            freteBO.emitir(f);
            resp.sendRedirect(req.getContextPath() + "/FreteControlador?acao=listar");
        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            req.setAttribute("frete", f);
            req.setAttribute("listaClientes",   clienteBO.listar(null, null, null, null, "ATIVO", 1, 999));
            req.setAttribute("listaMotoristas", motoristaBO.listar(null, 1, 999));
            req.setAttribute("listaVeiculos",   veiculoBO.listar(null, 1, 999));
            req.getRequestDispatcher("/WEB-INF/views/frete/formFrete.jsp").forward(req, resp);
        }
    }

    private void emTransito(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, NegocioException {
        int id = Integer.parseInt(req.getParameter("idFrete"));
        freteBO.registrarEmTransito(id, montarOcorrenciaDaRequisicao(req));
        resp.sendRedirect(req.getContextPath() + "/FreteControlador?acao=detalhe&id=" + id);
    }

    private void registrarEntrega(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, NegocioException {
        int id = Integer.parseInt(req.getParameter("idFrete"));
        freteBO.registrarEntrega(id, montarOcorrenciaDaRequisicao(req));
        resp.sendRedirect(req.getContextPath() + "/FreteControlador?acao=detalhe&id=" + id);
    }

    private void naoEntregue(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, NegocioException {
        int id = Integer.parseInt(req.getParameter("idFrete"));
        freteBO.registrarNaoEntrega(id, montarOcorrenciaDaRequisicao(req));
        resp.sendRedirect(req.getContextPath() + "/FreteControlador?acao=detalhe&id=" + id);
    }

    private void ocorrencia(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, NegocioException {
        int id = Integer.parseInt(req.getParameter("idFrete"));
        OcorrenciaFrete oc = montarOcorrenciaDaRequisicao(req);

        // TENTATIVA_ENTREGA deve ir para registrarNaoEntrega
        if (oc.getTipo() == OcorrenciaFrete.Tipo.TENTATIVA_ENTREGA) {
            freteBO.registrarNaoEntrega(id, oc);
        } else {
            freteBO.registrarOcorrencia(id, oc);
        }
        resp.sendRedirect(req.getContextPath() + "/FreteControlador?acao=detalhe&id=" + id);
    }

    private Frete montarFreteDaRequisicao(HttpServletRequest req) {
        Frete f = new Frete();
        String idRem = req.getParameter("idRemetente");
        if (idRem != null && !idRem.isEmpty()) {
            Cliente c = new Cliente(); c.setId(Integer.parseInt(idRem)); f.setRemetente(c);
        }
        String idDest = req.getParameter("idDestinatario");
        if (idDest != null && !idDest.isEmpty()) {
            Cliente c = new Cliente(); c.setId(Integer.parseInt(idDest)); f.setDestinatario(c);
        }
        String idMot = req.getParameter("idMotorista");
        if (idMot != null && !idMot.isEmpty()) {
            Motorista m = new Motorista(); m.setId(Integer.parseInt(idMot)); f.setMotorista(m);
        }
        String idVei = req.getParameter("idVeiculo");
        if (idVei != null && !idVei.isEmpty()) {
            Veiculo v = new Veiculo(); v.setId(Integer.parseInt(idVei)); f.setVeiculo(v);
        }
        f.setMunicipioOrigem(req.getParameter("municipioOrigem"));
        f.setUfOrigem(req.getParameter("ufOrigem"));
        f.setMunicipioDestino(req.getParameter("municipioDestino"));
        f.setUfDestino(req.getParameter("ufDestino"));
        f.setDescricaoCarga(req.getParameter("descricaoCarga"));
        String peso = req.getParameter("pesoKg");
        if (peso != null && !peso.isEmpty()) f.setPesoKg(new BigDecimal(peso.replace(",",".")));
        String vol = req.getParameter("volumes");
        if (vol != null && !vol.isEmpty()) f.setVolumes(Integer.parseInt(vol));
        String vf = req.getParameter("valorFrete");
        if (vf != null && !vf.isEmpty()) f.setValorFrete(new BigDecimal(vf.replace(",",".")));
        String aliq = req.getParameter("aliquotaIcms");
        if (aliq != null && !aliq.isEmpty()) f.setAliquotaIcms(new BigDecimal(aliq.replace(",",".")));
        String prev = req.getParameter("dataPrevisaoEntrega");
        if (prev != null && !prev.isEmpty()) f.setDataPrevisaoEntrega(LocalDate.parse(prev));
        return f;
    }

    private OcorrenciaFrete montarOcorrenciaDaRequisicao(HttpServletRequest req) {
        OcorrenciaFrete oc = new OcorrenciaFrete();
        String tipo = req.getParameter("tipo");
        if (tipo != null && !tipo.isEmpty()) oc.setTipo(OcorrenciaFrete.Tipo.valueOf(tipo));
        String dh = req.getParameter("dataHora");
        if (dh != null && !dh.isEmpty()) oc.setDataHora(LocalDateTime.parse(dh));
        oc.setMunicipio(req.getParameter("municipio"));
        oc.setUf(req.getParameter("uf"));
        oc.setDescricao(req.getParameter("descricao"));
        oc.setNomeRecebedor(req.getParameter("nomeRecebedor"));
        oc.setDocumentoRecebedor(req.getParameter("documentoRecebedor"));
        return oc;
    }

    private int parsePagina(String valor) {
        try { return Math.max(1, Integer.parseInt(valor)); }
        catch (Exception e) { return 1; }
    }

    private int parseIdMotorista(String valor) throws NegocioException {
        try {
            int id = Integer.parseInt(valor);
            if (id <= 0) throw new NumberFormatException();
            return id;
        } catch (Exception e) {
            throw new NegocioException("Motorista é obrigatório para gerar o relatório.");
        }
    }

    private LocalDate parseDataObrigatoria(String valor, String campo) throws NegocioException {
        if (valor == null || valor.trim().isEmpty()) {
            throw new NegocioException(campo + " é obrigatória para gerar o relatório.");
        }
        try {
            return LocalDate.parse(valor);
        } catch (Exception e) {
            throw new NegocioException(campo + " inválida para gerar o relatório.");
        }
    }
}
