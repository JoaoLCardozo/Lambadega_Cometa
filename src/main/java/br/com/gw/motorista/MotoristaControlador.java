package br.com.gw.motorista;

import br.com.gw.exception.CadastroException;
import br.com.gw.exception.NegocioException;
import br.com.gw.exception.RecursoNaoEncontradoException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.logging.Logger;

@WebServlet("/MotoristaControlador")
public class MotoristaControlador extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(MotoristaControlador.class.getName());
    private static final int LIMITE = 10;
    private static final String SUCESSO_MOTORISTA = "motoristaSucesso";

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
        } catch (RecursoNaoEncontradoException e) {
            responderNaoEncontrado(req, resp, e.getMessage());
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
            req.getRequestDispatcher("/WEB-INF/views/motorista/formMotorista.jsp").forward(req, resp);
        } catch (Exception e) {
            logger.severe("Erro inesperado POST: " + e.getMessage());
            System.err.println("[MotoristaControlador] " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/erro.jsp");
        }
    }

    private void listar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException, NegocioException {
        String filtro       = req.getParameter("filtro");
        String cpf          = req.getParameter("cpf");
        String status       = req.getParameter("status");
        String tipoVinculo  = req.getParameter("tipoVinculo");
        String cnhCategoria = req.getParameter("cnhCategoria");
        int pagina          = parsePagina(req.getParameter("pagina"));
        List<Motorista> lista  = motoristaBO.listar(
            filtro, cpf, status, tipoVinculo, cnhCategoria, pagina, LIMITE);
        int totalPaginas       = motoristaBO.contarPaginas(
            filtro, cpf, status, tipoVinculo, cnhCategoria, LIMITE);
        req.setAttribute("listaMotoristas", lista);
        req.setAttribute("filtro",          filtro);
        req.setAttribute("cpf",             cpf);
        req.setAttribute("status",          status);
        req.setAttribute("tipoVinculo",     tipoVinculo);
        req.setAttribute("cnhCategoria",    cnhCategoria);
        req.setAttribute("paginaAtual",     pagina);
        req.setAttribute("totalPaginas",    totalPaginas);
        carregarMensagemSucesso(req);
        req.getRequestDispatcher("/WEB-INF/views/motorista/listarMotorista.jsp").forward(req, resp);
    }

    private void novo(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("motorista", new Motorista());
        req.getRequestDispatcher("/WEB-INF/views/motorista/formMotorista.jsp").forward(req, resp);
    }

    private void editar(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException, NegocioException {
        int id = Integer.parseInt(req.getParameter("id"));
        req.setAttribute("motorista", motoristaBO.buscarPorId(id));
        req.getRequestDispatcher("/WEB-INF/views/motorista/formMotorista.jsp").forward(req, resp);
    }

    private void excluir(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, NegocioException {
        int id = Integer.parseInt(req.getParameter("id"));
        motoristaBO.excluir(id);
        resp.sendRedirect(req.getContextPath() + "/MotoristaControlador?acao=listar");
    }

    private void salvar(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, NegocioException {
        validarCamposDaRequisicao(req);
        motoristaBO.salvar(montarMotoristaDaRequisicao(req));
        req.getSession().setAttribute(SUCESSO_MOTORISTA, "Motorista cadastrado com sucesso!");
        resp.sendRedirect(req.getContextPath() + "/MotoristaControlador?acao=listar");
    }

    private void atualizar(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, NegocioException {
        validarCamposDaRequisicao(req);
        Motorista m = montarMotoristaDaRequisicao(req);
        m.setId(Integer.parseInt(req.getParameter("id")));
        motoristaBO.atualizar(m);
        req.getSession().setAttribute(SUCESSO_MOTORISTA, "Motorista atualizado com sucesso!");
        resp.sendRedirect(req.getContextPath() + "/MotoristaControlador?acao=listar");
    }

    private Motorista montarMotoristaDaRequisicao(HttpServletRequest req) {
        Motorista m = new Motorista();
        String id = req.getParameter("id");
        if (id != null && !id.isEmpty()) m.setId(Integer.parseInt(id));
        m.setNome(req.getParameter("nome"));
        m.setCpf(somenteDigitos(req.getParameter("cpf")));
        String dn = req.getParameter("dataNascimento");
        if (dn != null && !dn.isEmpty()) m.setDataNascimento(parseDataOpcional(dn));
        m.setTelefone(somenteDigitos(req.getParameter("telefone")));
        m.setCnhNumero(somenteDigitos(req.getParameter("cnhNumero")));
        String cat = req.getParameter("cnhCategoria");
        if (cat != null && !cat.isEmpty()) m.setCnhCategoria(Motorista.CnhCategoria.valueOf(cat));
        String val = req.getParameter("cnhValidade");
        if (val != null && !val.isEmpty()) m.setCnhValidade(parseDataOpcional(val));
        String tv = req.getParameter("tipoVinculo");
        if (tv != null && !tv.isEmpty()) m.setTipoVinculo(Motorista.TipoVinculo.valueOf(tv));
        String st = req.getParameter("status");
        m.setStatus(st != null && !st.isEmpty()
            ? Motorista.Status.valueOf(st) : Motorista.Status.ATIVO);
        return m;
    }

    private void validarCamposDaRequisicao(HttpServletRequest req) throws CadastroException {
        validarDataOpcional(req.getParameter("dataNascimento"), "Data de Nascimento");
        validarDataOpcional(req.getParameter("cnhValidade"), "Validade da CNH");

        String cnhNumero = req.getParameter("cnhNumero");
        if (cnhNumero != null && !cnhNumero.trim().isEmpty()
                && !cnhNumero.trim().matches("\\d{11}")) {
            throw new CadastroException("O Número da CNH deve conter exatamente 11 dígitos numéricos.");
        }
    }

    private void validarDataOpcional(String valor, String nomeCampo) throws CadastroException {
        String data = valor != null ? valor.trim() : "";
        if (data.isEmpty()) return;

        try {
            LocalDate.parse(data);
        } catch (DateTimeParseException e) {
            throw new CadastroException("O campo " + nomeCampo + " possui uma data inválida.");
        }
    }

    private LocalDate parseDataOpcional(String valor) {
        String data = valor != null ? valor.trim() : "";
        if (data.isEmpty()) return null;

        try {
            return LocalDate.parse(data);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private String somenteDigitos(String valor) {
        return valor != null ? valor.replaceAll("[^0-9]", "") : null;
    }

    private int parsePagina(String valor) {
        try { return Math.max(1, Integer.parseInt(valor)); }
        catch (Exception e) { return 1; }
    }

    private void carregarMensagemSucesso(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) return;

        Object sucesso = session.getAttribute(SUCESSO_MOTORISTA);
        if (sucesso != null) {
            req.setAttribute("sucesso", sucesso);
            session.removeAttribute(SUCESSO_MOTORISTA);
        }
    }

    private void responderNaoEncontrado(HttpServletRequest req, HttpServletResponse resp, String mensagem)
            throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        req.setAttribute("tituloErro", "Registro não encontrado");
        req.setAttribute("mensagemErro", mensagem);
        req.getRequestDispatcher("/erro.jsp").forward(req, resp);
    }
}
