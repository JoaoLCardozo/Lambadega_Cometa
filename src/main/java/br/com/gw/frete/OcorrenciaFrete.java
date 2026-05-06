package br.com.gw.frete;

import br.com.gw.util.DateUtils;

import java.io.Serializable;
import java.time.LocalDateTime;

public class OcorrenciaFrete implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Tipo {
        SAIDA_DO_PATIO, EM_ROTA, TENTATIVA_ENTREGA,
        ENTREGA_REALIZADA, AVARIA, EXTRAVIO, OUTROS
    }

    private int           id;
    private int           idFrete;
    private Tipo          tipo;
    private LocalDateTime dataHora;
    private String        municipio;
    private String        uf;
    private String        descricao;
    private String        nomeRecebedor;
    private String        documentoRecebedor;

    public OcorrenciaFrete() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdFrete() { return idFrete; }
    public void setIdFrete(int idFrete) { this.idFrete = idFrete; }

    public Tipo getTipo() { return tipo; }
    public void setTipo(Tipo tipo) { this.tipo = tipo; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public String getDataHoraFormatada() { return DateUtils.formatarDataHora(dataHora); }

    public String getMunicipio() { return municipio; }
    public void setMunicipio(String municipio) { this.municipio = municipio; }

    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getNomeRecebedor() { return nomeRecebedor; }
    public void setNomeRecebedor(String nomeRecebedor) { this.nomeRecebedor = nomeRecebedor; }

    public String getDocumentoRecebedor() { return documentoRecebedor; }
    public void setDocumentoRecebedor(String documentoRecebedor) { this.documentoRecebedor = documentoRecebedor; }
}
