package br.com.gw.monitorfretes;

public class MonitorFretesAlerta {
    private String nivel;
    private String titulo;
    private String descricao;
    private String link;
    private String acao;

    public MonitorFretesAlerta(String nivel, String titulo, String descricao, String link, String acao) {
        this.nivel = nivel;
        this.titulo = titulo;
        this.descricao = descricao;
        this.link = link;
        this.acao = acao;
    }

    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    public String getAcao() { return acao; }
    public void setAcao(String acao) { this.acao = acao; }
}
