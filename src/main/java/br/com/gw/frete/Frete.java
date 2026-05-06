package br.com.gw.frete;

import br.com.gw.cliente.Cliente;
import br.com.gw.motorista.Motorista;
import br.com.gw.util.DateUtils;
import br.com.gw.veiculo.Veiculo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Frete implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status {
        EMITIDO, SAIDA_CONFIRMADA, EM_TRANSITO, ENTREGUE, NAO_ENTREGUE, CANCELADO
    }

    private int            id;
    private String         numero;
    private Cliente        remetente;
    private Cliente        destinatario;
    private Motorista      motorista;
    private Veiculo        veiculo;
    private String         municipioOrigem;
    private String         ufOrigem;
    private String         municipioDestino;
    private String         ufDestino;
    private String         descricaoCarga;
    private BigDecimal     pesoKg;
    private int            volumes;
    private BigDecimal     valorFrete;
    private BigDecimal     aliquotaIcms;
    private BigDecimal     valorIcms;
    private BigDecimal     valorTotal;
    private Status         status;
    private LocalDateTime  dataEmissao;
    private LocalDate      dataPrevisaoEntrega;
    private LocalDateTime  dataSaida;
    private LocalDateTime  dataEntrega;
    private List<OcorrenciaFrete> ocorrencias;

    public Frete() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public Cliente getRemetente() { return remetente; }
    public void setRemetente(Cliente remetente) { this.remetente = remetente; }

    public Cliente getDestinatario() { return destinatario; }
    public void setDestinatario(Cliente destinatario) { this.destinatario = destinatario; }

    public Motorista getMotorista() { return motorista; }
    public void setMotorista(Motorista motorista) { this.motorista = motorista; }

    public Veiculo getVeiculo() { return veiculo; }
    public void setVeiculo(Veiculo veiculo) { this.veiculo = veiculo; }

    public String getMunicipioOrigem() { return municipioOrigem; }
    public void setMunicipioOrigem(String municipioOrigem) { this.municipioOrigem = municipioOrigem; }

    public String getUfOrigem() { return ufOrigem; }
    public void setUfOrigem(String ufOrigem) { this.ufOrigem = ufOrigem; }

    public String getMunicipioDestino() { return municipioDestino; }
    public void setMunicipioDestino(String municipioDestino) { this.municipioDestino = municipioDestino; }

    public String getUfDestino() { return ufDestino; }
    public void setUfDestino(String ufDestino) { this.ufDestino = ufDestino; }

    public String getDescricaoCarga() { return descricaoCarga; }
    public void setDescricaoCarga(String descricaoCarga) { this.descricaoCarga = descricaoCarga; }

    public BigDecimal getPesoKg() { return pesoKg; }
    public void setPesoKg(BigDecimal pesoKg) { this.pesoKg = pesoKg; }

    public int getVolumes() { return volumes; }
    public void setVolumes(int volumes) { this.volumes = volumes; }

    public BigDecimal getValorFrete() { return valorFrete; }
    public void setValorFrete(BigDecimal valorFrete) { this.valorFrete = valorFrete; }

    public BigDecimal getAliquotaIcms() { return aliquotaIcms; }
    public void setAliquotaIcms(BigDecimal aliquotaIcms) { this.aliquotaIcms = aliquotaIcms; }

    public BigDecimal getValorIcms() { return valorIcms; }
    public void setValorIcms(BigDecimal valorIcms) { this.valorIcms = valorIcms; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDateTime getDataEmissao() { return dataEmissao; }
    public void setDataEmissao(LocalDateTime dataEmissao) { this.dataEmissao = dataEmissao; }
    public String getDataEmissaoFormatada() { return DateUtils.formatarDataHora(dataEmissao); }

    public LocalDate getDataPrevisaoEntrega() { return dataPrevisaoEntrega; }
    public void setDataPrevisaoEntrega(LocalDate dataPrevisaoEntrega) { this.dataPrevisaoEntrega = dataPrevisaoEntrega; }
    public String getDataPrevisaoEntregaFormatada() { return DateUtils.formatarData(dataPrevisaoEntrega); }

    public LocalDateTime getDataSaida() { return dataSaida; }
    public void setDataSaida(LocalDateTime dataSaida) { this.dataSaida = dataSaida; }
    public String getDataSaidaFormatada() { return DateUtils.formatarDataHora(dataSaida); }

    public LocalDateTime getDataEntrega() { return dataEntrega; }
    public void setDataEntrega(LocalDateTime dataEntrega) { this.dataEntrega = dataEntrega; }
    public String getDataEntregaFormatada() { return DateUtils.formatarDataHora(dataEntrega); }

    public List<OcorrenciaFrete> getOcorrencias() { return ocorrencias; }
    public void setOcorrencias(List<OcorrenciaFrete> ocorrencias) { this.ocorrencias = ocorrencias; }

    public boolean podeReceberOcorrencia() {
        return status != Status.ENTREGUE
            && status != Status.NAO_ENTREGUE
            && status != Status.CANCELADO;
    }

    @Override
    public String toString() {
        return "Frete{id=" + id + ", numero='" + numero + "', status=" + status + "}";
    }
}
