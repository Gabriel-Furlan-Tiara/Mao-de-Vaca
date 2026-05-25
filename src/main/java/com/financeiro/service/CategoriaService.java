package com.financeiro.service;
import com.financeiro.model.Categoria;
import com.financeiro.repository.CategoriaRepository;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class CategoriaService {
    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
    }
    //CRIA NOVA CATEGORIA COM NOME
    //SE NOME FOR VAZIO AVISA
    public Categoria criar(Categoria categoria) throws SQLException {
        if (categoria.getNome() == null || categoria.getNome().isEmpty()) {
            throw new IllegalArgumentException("É obrigatório por nome na categoria");
        }
        //TIPO DA CATEGORIA
        validarTipo(categoria.getTipo());

        //NAO PODE TER MAIS DE UMA CATEGORIA COM O MESMO NOME
        Optional<Categoria> existente = repository.buscarPorNome(categoria.getNome());
        if (existente.isPresent()) {
            throw new IllegalStateException("Já existe uma categoria cadastrada com esse nome");
        }
        return repository.salvar(categoria);
    }

    //LISTA TODAS AS CATEGORIAS CADASTRADAS
    public List<Categoria> listarTodas() throws SQLException {
        return repository.buscarTodas();
    }

    //VERIFICA SE A CATEGORIA EXISTE ANTES DE EDITAR
    public Categoria editar(int id, Categoria atualizada) throws SQLException {
        Optional<Categoria> encontrada = repository.buscarPorId(id);
        if (encontrada.isEmpty()) {
            throw new IllegalArgumentException("Categoria com ID = " + id + " não foi encontrada.");
        }
        if (atualizada.getNome() == null || atualizada.getNome().isBlank()) {
            throw new IllegalArgumentException("É obrigatório por nome na categoria");
        }
        validarTipo(atualizada.getTipo());

        //VERIFICA SE EXISTE DUPLICADA
        Optional<Categoria> mesmoNome = repository.buscarPorNome(atualizada.getNome());
        if (mesmoNome.isPresent() && mesmoNome.get().getId() != id) {
            throw new IllegalStateException("Já existe uma categoria cadastrada com esse nome");
        }
        //DEFINE O ID E ATUALIZA NO BANCO
        atualizada.setId(id);
        repository.atualizar(atualizada);
        return atualizada;
    }

    //EXCLUI UMA CATEGORIA POR ID
    public void excluir(int id) throws SQLException {
        Optional<Categoria> encontrada = repository.buscarPorId(id);
        if (encontrada.isEmpty()) {
            throw new IllegalArgumentException("Categoria com ID = " + id + " não foi encontrada.");
        }
        //NÃO PERMITE EXCLUIR SE ESTIVER EM USO
        if (repository.possuiTransacoesVinculadas(id)) {
            throw new IllegalArgumentException("Não foi possível excluir, categoria esta vinculada a uma transação");
        }
        //EXCLUI APÓS VALIDAÇÃO
        repository.excluir(id);
    }

    //VALIDA SE O TIPO INFORMADO VAI SER ACEITO
    private void validarTipo(String tipo) {
        if (tipo == null || (!tipo.equals("RECEITA") && !tipo.equals("DESPESA") && !tipo.equals("AMBOS"))) {
            throw new IllegalArgumentException("O tipo deve ser RECEITA, DESPESA ou AMBOS.");
        }
    }
}

