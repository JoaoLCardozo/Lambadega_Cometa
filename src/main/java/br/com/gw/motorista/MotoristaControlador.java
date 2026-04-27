package br.com.gw.motorista;

import br.com.gw.exception.NegocioException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

@WebServlet("/MotoristaControlador")
public class MotoristaControlador extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(MotoristaControlador.class.getName());
    private static final int LIMITE = 10;

    private final MotoristaBO motoristaBO = new MotoristaBO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String acao = req.getParameter("acao");
        if (acao == null) acao = "listar";
        try {
            switch (acao) {
                case "listar":  listar(req, resp);  break;
                case "novo":    novo(req, resp);     break;
                case "editar":  editar(req, resp);   break;
                case "excluir": excluir(req, resp);  break;
                default:        listar(req, resp);
            }
        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            try { listar(req, resp); } catch (NegocioException ex) {
                logger.severe("Erro ao recarregar listagem: " + ex.getMessage());
            }
        } catch (Exception e) {
            logger.severe("Erro inesperado: " + e.getMessage());
            System.err.println("[MotoristaControlador] " + e.getMessage());
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
                case "salvar":    salvar(req, resp);    break;
                case "atualizar": atualizar(req, resp); break;
                default:
                    resp.sendRedirect(req.getContextPath() + "/MotoristaControlador?acao=listar");
            }
        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            req.setAttribute("motorista", montarMotoristaDaRequisicao(req));
            req.getRequestDispatcher("/WEB-INF/views/motorista/form.jsp").forward(req, resp);
        } catch (Exception e) {
            logger.severe("Erro inesperado POST: " + e.getMessage());
            System.err.println("[MotoristaControlador] " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/erro.jsp");
        }
    }

    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException, NegocioException {
        String filtro    = req.getParameter("filtro");
        int pagina       = parsePagina(req.getParameter("pagina"));
        List<Motorista> lista  = motoristaBO.listar(filtro, pagina, LIMITE);
        int totalPaginas       = motoristaBO.contarPaginas(filtro, LIMITE);
        req.setAttribute("listaMotoristas", lista);
        req.setAttribute("filtro",          filtro);
        req.setAttribute("paginaAtual",     pagina);
        req.setAttribute("totalPaginas",    totalPaginas);
        req.getRequestDispatcher("/WEB-INF/views/motorista/lista.jsp").forward(req, resp);
    }

    private void novo(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("motorista", new Motorista());
        req.getRequestDispatcher("/WEB-INF/views/motorista/form.jsp").forward(req, resp);
    }

    private void editar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException, NegocioException {
        int id = Integer.parseInt(req.getParameter("id"));
        req.setAttribute("motorista", motoristaBO.buscarPorId(id));
        req.getRequestDispatcher("/WEB-INF/views/motorista/form.jsp").forward(req, resp);
    }

    private void excluir(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, NegocioException {
        int id = Integer.parseInt(req.getParameter("id"));
        motoristaBO.excluir(id);
        resp.sendRedirect(req.getContextPath() + "/MotoristaControlador?acao=listar");
    }

    private void salvar(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, NegocioException {
        motoristaBO.salvar(montarMotoristaDaRequisicao(req));
        resp.sendRedirect(req.getContextPath() + "/MotoristaControlador?acao=listar");
    }

    private void atualizar(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, NegocioException {
        Motorista m = montarMotoristaDaRequisicao(req);
        m.setId(Integer.parseInt(req.getParameter("id")));
        motoristaBO.atualizar(m);
        resp.sendRedirect(req.getContextPath() + "/MotoristaControlador?acao=listar");
    }

    private Motorista montarMotoristaDaRequisicao(HttpServletRequest req) {
        Motorista m = new Motorista();
        m.setNome(req.getParameter("nome"));
        m.setCpf(req.getParameter("cpf"));
        String dn = req.getParameter("dataNascimento");
        if (dn != null && !dn.isEmpty()) m.setDataNascimento(LocalDate.parse(dn));
        m.setTelefone(req.getParameter("telefone"));
        m.setCnhNumero(req.getParameter("cnhNumero"));
        String cat = req.getParameter("cnhCategoria");
        if (cat != null && !cat.isEmpty()) m.setCnhCategoria(Motorista.CnhCategoria.valueOf(cat));
        String val = req.getParameter("cnhValidade");
        if (val != null && !val.isEmpty()) m.setCnhValidade(LocalDate.parse(val));
        String tv = req.getParameter("tipoVinculo");
        if (tv != null && !tv.isEmpty()) m.setTipoVinculo(Motorista.TipoVinculo.valueOf(tv));
        String st = req.getParameter("status");
        m.setStatus(st != null && !st.isEmpty()
            ? Motorista.Status.valueOf(st) : Motorista.Status.ATIVO);
        return m;
    }

    private int parsePagina(String valor) {
        try { return Math.max(1, Integer.parseInt(valor)); }
        catch (Exception e) { return 1; }
    }
}