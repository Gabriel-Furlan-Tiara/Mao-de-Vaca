package com.financeiro.service;
import com.financeiro.model.Categoria;
import com.financeiro.model.RelatorioMensal;
import com.financeiro.model.Transacao;
import com.financeiro.repository.CategoriaRepository;
import com.financeiro.repository.TransacaoRepository;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RelatorioService {
    private final TransacaoRepository transacaoRepository;
    private final CategoriaRepository categoriaRepository;

    public RelatorioService(TransacaoRepository transacaoRepository, CategoriaRepository categoriaRepository) {
        this.transacaoRepository = transacaoRepository;
        this.categoriaRepository = categoriaRepository;
    }
    // Calcula os dados do relatorio e devolve um objeto RelatorioMensal.
    // Usado pelo frontend (via RelatorioHandler) para montar os cards.
    public RelatorioMensal gerarDados(int mes, int ano) throws SQLException {

        // RN11: valida o mes
        if (mes < 1 || mes > 12) {
            throw new IllegalArgumentException("O mes deve estar entre 1 e 12.");
        }
        RelatorioMensal r = new RelatorioMensal();
        r.setMes(mes);
        r.setAno(ano);
        List<Transacao> transacoes = transacaoRepository.buscarPorMesAno(mes, ano);
        r.setQuantidadeTransacoes(transacoes.size());

        // RN13: se nao houver transacoes
        if (transacoes.isEmpty()) {
            r.setSituacao("SEM_MOVIMENTACAO");
            r.setCategoriaMaiorGasto("-");
            return r;
        }
        double totalReceitas = 0;
        double totalDespesas = 0;
        Map<Integer, Double> despesasPorCategoria = new HashMap<>();
        for (Transacao t : transacoes) {
            if (t.getTipo().equals("RECEITA")) {
                totalReceitas += t.getValor();
            } else { // DESPESA
                totalDespesas += t.getValor();
                despesasPorCategoria.merge(t.getCategoriaId(), t.getValor(), Double::sum);
            }
        }
        double saldo = totalReceitas - totalDespesas; // RN12
        String situacao = saldo > 0 ? "POSITIVO" : (saldo < 0 ? "NEGATIVO" : "NEUTRO");
        r.setTotalReceitas(totalReceitas);
        r.setTotalDespesas(totalDespesas);
        r.setSaldo(saldo);
        r.setSituacao(situacao);

        // descobre a categoria com mais gastos
        if (!despesasPorCategoria.isEmpty()) {
            int categoriaMaiorId = 0;
            double maiorValor = 0;
            for (Map.Entry<Integer, Double> entrada : despesasPorCategoria.entrySet()) {
                if (entrada.getValue() > maiorValor) {
                    maiorValor = entrada.getValue();
                    categoriaMaiorId = entrada.getKey();
                }
            }
            String nomeCategoria = categoriaRepository.buscarPorId(categoriaMaiorId)
                    .map(Categoria::getNome)
                    .orElse("Desconhecida");
            r.setCategoriaMaiorGasto(nomeCategoria);
            r.setValorMaiorGasto(maiorValor);
        } else {
            r.setCategoriaMaiorGasto("-");
        }
        return r;
    }

    /* Versao em texto, usada pelo menu de console.
    public String gerarRelatorioMensal(int mes, int ano) throws SQLException {
        RelatorioMensal r = gerarDados(mes, ano);
        if (r.getSituacao().equals("SEM_MOVIMENTACAO")) {
            return "Nenhuma transacao encontrada para " + mes + "/" + ano + ".";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\n========================================\n");
        sb.append("   RELATORIO MENSAL - ").append(mes).append("/").append(ano).append("\n");
        sb.append("========================================\n");
        sb.append(String.format("Total de receitas: R$ %.2f%n", r.getTotalReceitas()));
        sb.append(String.format("Total de despesas: R$ %.2f%n", r.getTotalDespesas()));
        sb.append(String.format("Saldo do mes:      R$ %.2f  (%s)%n", r.getSaldo(), r.getSituacao()));
        if (r.getValorMaiorGasto() > 0) {
            sb.append("----------------------------------------\n");
            sb.append(String.format("Maior gasto: %s (R$ %.2f)%n",
                    r.getCategoriaMaiorGasto(), r.getValorMaiorGasto()));
        }
        sb.append("Total de transacoes no mes: ").append(r.getQuantidadeTransacoes()).append("\n");
        sb.append("========================================\n");
        return sb.toString();
    }

     */
}