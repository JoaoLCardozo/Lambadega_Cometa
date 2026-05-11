package br.com.gw.util;

import br.com.gw.exception.NegocioException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailService {
    private static final Logger logger = Logger.getLogger(EmailService.class.getName());

    private final Properties config;

    public EmailService() {
        this.config = carregarConfiguracoes();
    }

    public void enviarCodigoRecuperacao(String destino, String nomeUsuario, String codigo)
            throws NegocioException {
        String assunto = "Código de recuperação de senha - Lambadega Cometa";
        String nome = nomeUsuario != null && !nomeUsuario.trim().isEmpty()
            ? nomeUsuario.trim()
            : "usuário";
        String corpo = "Olá, " + nome + ".\n\n"
            + "Seu código de verificação para trocar a senha é: " + codigo + "\n\n"
            + "Este código expira em 15 minutos. Se você não solicitou a recuperação, ignore este e-mail.\n\n"
            + "Lambadega Cometa";

        enviar(destino, assunto, corpo);
    }

    private void enviar(String destino, String assunto, String corpo) throws NegocioException {
        final String host = obter("email.smtp.host", "EMAIL_SMTP_HOST");
        final String port = obterOuPadrao("email.smtp.port", "EMAIL_SMTP_PORT", "587");
        final String usuario = obter("email.usuario", "EMAIL_USUARIO");
        final String senha = normalizarSenha(obter("email.senha", "EMAIL_SENHA"));
        final String remetente = obter("email.remetente", "EMAIL_REMETENTE");
        final String nomeRemetente = obterOuPadrao(
            "email.nomeRemetente", "EMAIL_NOME_REMETENTE", "Lambadega Cometa");
        boolean auth = Boolean.parseBoolean(obterOuPadrao(
            "email.smtp.auth", "EMAIL_SMTP_AUTH", "true"));
        boolean startTls = Boolean.parseBoolean(obterOuPadrao(
            "email.smtp.starttls.enable", "EMAIL_SMTP_STARTTLS", "true"));

        if (vazio(host) || vazio(remetente) || (auth && (vazio(usuario) || vazio(senha)))) {
            throw new NegocioException(
                "Envio de e-mail não configurado. Verifique o arquivo email.properties.");
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", String.valueOf(auth));
        props.put("mail.smtp.starttls.enable", String.valueOf(startTls));
        props.put("mail.smtp.ssl.trust", host);
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");

        Session session = auth
            ? Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(usuario, senha);
                }
            })
            : Session.getInstance(props);

        try {
            Message mensagem = new MimeMessage(session);
            mensagem.setFrom(new InternetAddress(remetente, nomeRemetente));
            mensagem.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destino));
            mensagem.setSubject(assunto);
            mensagem.setText(corpo);
            Transport.send(mensagem);
        } catch (AuthenticationFailedException e) {
            logger.warning("Falha de autenticação SMTP: " + e.getMessage());
            throw new NegocioException(
                "Falha na autenticação do e-mail. Verifique o e-mail remetente e a senha de app.", e);
        } catch (MessagingException | UnsupportedEncodingException e) {
            logger.warning("Erro ao enviar e-mail: " + mensagemErro(e));
            throw new NegocioException(
                "Não foi possível enviar o e-mail de recuperação. Verifique conexão, SMTP e senha de app.", e);
        }
    }

    private Properties carregarConfiguracoes() {
        Properties props = new Properties();
        try (InputStream is = EmailService.class
                .getClassLoader().getResourceAsStream("email.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (IOException e) {
            logger.warning("Erro ao ler email.properties: " + e.getMessage());
        }
        return props;
    }

    private String obter(String chave, String ambiente) {
        String valorAmbiente = System.getenv(ambiente);
        if (!vazio(valorAmbiente)) return valorAmbiente.trim();
        String valor = config.getProperty(chave);
        return valor != null ? valor.trim() : null;
    }

    private String obterOuPadrao(String chave, String ambiente, String padrao) {
        String valor = obter(chave, ambiente);
        return vazio(valor) ? padrao : valor;
    }

    private boolean vazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private String normalizarSenha(String senha) {
        return senha != null ? senha.replaceAll("\\s+", "") : null;
    }

    private String mensagemErro(Exception e) {
        Throwable causa = e;
        while (causa.getCause() != null) {
            causa = causa.getCause();
        }
        return causa.getMessage() != null ? causa.getMessage() : e.getMessage();
    }
}
