package com.financeiro.service;
import com.financeiro.model.Transacao;
import com.financeiro.repository.CategoriaRepository;
import com.financeiro.repository.TransacaoRepository;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class TransacaoService {
    private final TransacaoRepository transacaoRepository;
    private final CategoriaRepository categoriaRepository;
    public TransacaoService(TransacaoRepository transacaoRepository, CategoriaRepository categoriaRepository) {
        this.transacaoRepository = transacaoRepository;
        this.categoriaRepository = categoriaRepository;
    }
    public Transacao registrar(Transacao transacao) throws SQLException {
        validar(transacao);
        return transacaoRepository.salvar(transacao);
    }
    public List<Transacao> listarTodas() throws SQLException {
        return transacaoRepository.buscarTodas();
    }
    public Transacao editar(int id, Transacao atualizada) throws SQLException {
        Optional<Transacao> encontrada = transacaoRepository.buscarPorId(id);
        if (encontrada.isEmpty()) {
            throw new IllegalArgumentException("Transação com ID " + id + " não encontrada.");
        }
        validar(atualizada);
        atualizada.setId(id);
        transacaoRepository.atualizar(atualizada);
        return atualizada;
    }
    public void excluir(int id) throws SQLException {
        Optional<Transacao> encontrada = transacaoRepository.buscarPorId(id);
        if (encontrada.isEmpty()) {
            throw new IllegalArgumentException("Transação com ID " + id + " não encontrada.");
        }
        transacaoRepository.excluir(id);
    }
    private void validar(Transacao transacao) throws SQLException {
        // RN04: tipo obrigatório
        if (transacao.getTipo() == null ||
                (!transacao.getTipo().equals("RECEITA") && !transacao.getTipo().equals("DESPESA"))) {
            throw new IllegalArgumentException("O tipo deve ser RECEITA ou DESPESA.");
        }
        // RN05: valor maior que zero
        if (transacao.getValor() <= 0) {
            throw new IllegalArgumentException("O valor deve ser maior que zero.");
        }
        // RN04: data obrigatória
        if (transacao.getData() == null || transacao.getData().isBlank()) {
            throw new IllegalArgumentException("A data é obrigatória.");
        }
        // RN06: data não pode ser futura
        if (LocalDate.parse(transacao.getData()).isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("A data não pode ser futura.");
        }
        // RN04: descrição obrigatória
        if (transacao.getDescricao() == null || transacao.getDescricao().isBlank()) {
            throw new IllegalArgumentException("A descrição é obrigatória.");
        }
        // RN07: categoria deve existir
        if (categoriaRepository.buscarPorId(transacao.getCategoriaId()).isEmpty()) {
            throw new IllegalArgumentException("Categoria não encontrada.");
        }
    }
}
