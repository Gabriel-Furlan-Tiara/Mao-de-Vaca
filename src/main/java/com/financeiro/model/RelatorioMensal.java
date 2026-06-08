package com.financeiro.model;
// Guarda os dados calculados do relatório mensal.
// Serve para o RelatorioHandler devolver tudo em JSON para o frontend.
public class RelatorioMensal {
    private int mes;
    private int ano;
    private double totalReceitas;
    private double totalDespesas;
    private double saldo;
    private String situacao;            // POSITIVO, NEGATIVO, NEUTRO ou SEM_MOVIMENTACAO
    private String categoriaMaiorGasto; // nome da categoria que mais gastou
    private double valorMaiorGasto;
    private int quantidadeTransacoes;
    public RelatorioMensal() {}

    public int getMes() { return mes; }
    public void setMes(int mes) { this.mes = mes; }

    public int getAno() { return ano; }
    public void setAno(int ano) { this.ano = ano; }

    public double getTotalReceitas() { return totalReceitas; }
    public void setTotalReceitas(double totalReceitas) { this.totalReceitas = totalReceitas; }

    public double getTotalDespesas() { return totalDespesas; }
    public void setTotalDespesas(double totalDespesas) { this.totalDespesas = totalDespesas; }

    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }

    public String getSituacao() { return situacao; }
    public void setSituacao(String situacao) { this.situacao = situacao; }

    public String getCategoriaMaiorGasto() { return categoriaMaiorGasto; }
    public void setCategoriaMaiorGasto(String categoriaMaiorGasto) { this.categoriaMaiorGasto = categoriaMaiorGasto; }

    public double getValorMaiorGasto() { return valorMaiorGasto; }
    public void setValorMaiorGasto(double valorMaiorGasto) { this.valorMaiorGasto = valorMaiorGasto; }

    public int getQuantidadeTransacoes() { return quantidadeTransacoes; }
    public void setQuantidadeTransacoes(int quantidadeTransacoes) { this.quantidadeTransacoes = quantidadeTransacoes; }
}
