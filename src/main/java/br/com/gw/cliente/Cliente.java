package br.com.gw.cliente;

import java.io.Serializable;

public class Cliente implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum TipoPessoa { F, J }
    public enum Status     { ATIVO, INATIVO }

    private int        id;
    private TipoPessoa tipoPessoa;
    private String     nomeRazaoSocial;
    private String     nomeFantasia;
    private String     documento;
    private String     inscricaoEstadual;

    // Endereço
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String municipio;
    private String uf;
    private String cep;

    // Contato
    private String telefone;
    private String email;

    private Status status;

    public Cliente() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public TipoPessoa getTipoPessoa() { return tipoPessoa; }
    public void setTipoPessoa(TipoPessoa tipoPessoa) { this.tipoPessoa = tipoPessoa; }

    public String getNomeRazaoSocial() { return nomeRazaoSocial; }
    public void setNomeRazaoSocial(String nomeRazaoSocial) { this.nomeRazaoSocial = nomeRazaoSocial; }

    public String getNomeFantasia() { return nomeFantasia; }
    public void setNomeFantasia(String nomeFantasia) { this.nomeFantasia = nomeFantasia; }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }

    public String getDocumentoFormatado() {
        String digitos = somenteDigitos(documento);

        if (tipoPessoa == TipoPessoa.F && digitos.length() == 11) {
            return digitos.substring(0, 3) + "."
                + digitos.substring(3, 6) + "."
                + digitos.substring(6, 9) + "-"
                + digitos.substring(9);
        }

        if (tipoPessoa == TipoPessoa.J && digitos.length() == 14) {
            return digitos.substring(0, 2) + "."
                + digitos.substring(2, 5) + "."
                + digitos.substring(5, 8) + "/"
                + digitos.substring(8, 12) + "-"
                + digitos.substring(12);
        }

        return documento;
    }

    public String getInscricaoEstadual() { return inscricaoEstadual; }
    public void setInscricaoEstadual(String inscricaoEstadual) { this.inscricaoEstadual = inscricaoEstadual; }

    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getMunicipio() { return municipio; }
    public void setMunicipio(String municipio) { this.municipio = municipio; }

    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    private String somenteDigitos(String valor) {
        return valor != null ? valor.replaceAll("[^0-9]", "") : "";
    }

    @Override
    public String toString() {
        return "Cliente{id=" + id + ", tipoPessoa=" + tipoPessoa
            + ", nomeRazaoSocial='" + nomeRazaoSocial
            + "', documento='" + documento
            + "', status=" + status + "}";
    }
}
