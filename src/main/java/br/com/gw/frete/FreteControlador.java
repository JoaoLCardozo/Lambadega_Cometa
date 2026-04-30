package br.com.gw.frete;

import br.com.gw.cliente.Cliente;
import br.com.gw.cliente.ClienteDAO;
import br.com.gw.exception.NegocioException;
import br.com.gw.motorista.Motorista;
import br.com.gw.motorista.MotoristaDAO;
import br.com.gw.veiculo.Veiculo;
import br.com.gw.veiculo.VeiculoDAO;

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

    private final FreteBO    freteBO    = new FreteBO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final MotoristaDAO motoristaDAO = new MotoristaDAO();
    private final VeiculoDAO veiculoDAO = new VeiculoDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String acao = req.getParameter("acao");
        if (acao == null) acao = "listar";
        try {
            switch (acao) {
                case "listar":           listar(req, resp);          break;
                case "novo":             novo(req, resp);             break;
                case "detalhe":          detalhe(req, resp);          break;
                case "confirmarSaida":   confirmarSaida(req, resp);   break;
                case "cancelar":         cancelar(req, resp);         break;
                case "novaOcorrencia":   novaOcorrencia(req, resp);   break;
                default:                 listar(req, resp);
            }
        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            try { listar(req, resp); } catch (NegocioException ex) {
                logger.severe("Erro ao recarregar listagem: " + ex.getMessage());
                req.setAttribute("erro", "Erro ao listar fretes: " + ex.getMessage());
                req.getRequestDispatcher("/erro.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            logger.severe("Erro inesperado: " + e.getMessage());
            e.printStackTrace();
            req.setAttribute("erro", "Erro inesperado: " + e.getMessage());
            req.getRequestDispatcher("/erro.jsp").forward(req, resp);
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
            logger.severe("Erro inesperado POST: " + e.getMessage());
            e.printStackTrace();
            req.setAttribute("erro", "Erro inesperado: " + e.getMessage());
            req.getRequestDispatcher("/erro.jsp").forward(req, resp);
        }
    }

    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException, NegocioException {
        String filtro    = req.getParameter("filtro");
        int pagina       = parsePagina(req.getParameter("pagina"));
        List<Frete> lista      = freteBO.listar(filtro, pagina, LIMITE);
        int totalPaginas       = freteBO.contarPaginas(filtro, LIMITE);
        req.setAttribute("listaFretes",   lista);
        req.setAttribute("filtro",        filtro);
        req.setAttribute("paginaAtual",   pagina);
        req.setAttribute("totalPaginas",  totalPaginas);
        req.getRequestDispatcher("/WEB-INF/views/frete/listarFrete.jsp").forward(req, resp);
    }

    private void novo(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException, NegocioException {
        req.setAttribute("listaClientes",   clienteDAO.listar(null, 1, 999));
        req.setAttribute("listaMotoristas", motoristaDAO.listar(null, 1, 999));
        req.setAttribute("listaVeiculos",   veiculoDAO.listar(null, 1, 999));
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
            req.setAttribute("listaClientes",   clienteDAO.listar(null, 1, 999));
            req.setAttribute("listaMotoristas", motoristaDAO.listar(null, 1, 999));
            req.setAttribute("listaVeiculos",   veiculoDAO.listar(null, 1, 999));
            req.getRequestDispatcher("/WEB-INF/views/frete/formFrete.jsp").forward(req, resp);
        }
    }

    private void emTransito(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, NegocioException {
        int id = Integer.parseInt(req.getParameter("idFrete"));
        OcorrenciaFrete oc = montarOcorrenciaDaRequisicao(req);
        freteBO.registrarEmTransito(id, oc);
        resp.sendRedirect(req.getContextPath() + "/FreteControlador?acao=detalhe&id=" + id);
    }

    private void registrarEntrega(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, NegocioException {
        int id = Integer.parseInt(req.getParameter("idFrete"));
        OcorrenciaFrete oc = montarOcorrenciaDaRequisicao(req);
        freteBO.registrarEntrega(id, oc);
        resp.sendRedirect(req.getContextPath() + "/FreteControlador?acao=detalhe&id=" + id);
    }

    private void naoEntregue(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, NegocioException {
        int id = Integer.parseInt(req.getParameter("idFrete"));
        OcorrenciaFrete oc = montarOcorrenciaDaRequisicao(req);
        freteBO.registrarNaoEntrega(id, oc);
        resp.sendRedirect(req.getContextPath() + "/FreteControlador?acao=detalhe&id=" + id);
    }

    private void ocorrencia(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, NegocioException {
        int id = Integer.parseInt(req.getParameter("idFrete"));
        OcorrenciaFrete oc = montarOcorrenciaDaRequisicao(req);
        freteBO.registrarOcorrencia(id, oc);
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
        if (peso != null && !peso.isEmpty()) f.setPesoKg(new BigDecimal(peso));
        String vol = req.getParameter("volumes");
        if (vol != null && !vol.isEmpty()) f.setVolumes(Integer.parseInt(vol));
        String vf = req.getParameter("valorFrete");
        if (vf != null && !vf.isEmpty()) f.setValorFrete(new BigDecimal(vf));
        String aliq = req.getParameter("aliquotaIcms");
        if (aliq != null && !aliq.isEmpty()) f.setAliquotaIcms(new BigDecimal(aliq));
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
}