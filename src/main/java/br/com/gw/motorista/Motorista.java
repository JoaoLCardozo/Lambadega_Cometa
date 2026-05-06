package br.com.gw.motorista;

import br.com.gw.util.DateUtils;

import java.io.Serializable;
import java.time.LocalDate;

public class Motorista implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum CnhCategoria { A, B, C, D, E }
    public enum TipoVinculo  { FUNCIONARIO, AGREGADO, TERCEIRO }
    public enum Status       { ATIVO, INATIVO, SUSPENSO }

    private int          id;
    private String       nome;
    private String       cpf;
    private LocalDate    dataNascimento;
    private String       telefone;
    private String       cnhNumero;
    private CnhCategoria cnhCategoria;
    private LocalDate    cnhValidade;
    private TipoVinculo  tipoVinculo;
    private Status       status;

    public Motorista() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    public String getDataNascimentoFormatada() { return DateUtils.formatarData(dataNascimento); }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getCnhNumero() { return cnhNumero; }
    public void setCnhNumero(String cnhNumero) { this.cnhNumero = cnhNumero; }

    public CnhCategoria getCnhCategoria() { return cnhCategoria; }
    public void setCnhCategoria(CnhCategoria cnhCategoria) { this.cnhCategoria = cnhCategoria; }

    public LocalDate getCnhValidade() { return cnhValidade; }
    public void setCnhValidade(LocalDate cnhValidade) { this.cnhValidade = cnhValidade; }
    public String getCnhValidadeFormatada() { return DateUtils.formatarData(cnhValidade); }

    public TipoVinculo getTipoVinculo() { return tipoVinculo; }
    public void setTipoVinculo(TipoVinculo tipoVinculo) { this.tipoVinculo = tipoVinculo; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public boolean isCnhVencida() {
        return cnhValidade != null && cnhValidade.isBefore(LocalDate.now());
    }

    @Override
    public String toString() {
        return "Motorista{id=" + id + ", nome='" + nome + "', cpf='" + cpf + "'}";
    }
}
