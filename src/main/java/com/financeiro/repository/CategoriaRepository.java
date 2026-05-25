package com.financeiro.repository;

import com.financeiro.database.DatabaseConnection;
import com.financeiro.model.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoriaRepository {

    // cadastra e salva uma categoria nova no banco
    public Categoria salvar(Categoria categoria) throws SQLException {
        String sql = "INSERT INTO categorias (nome, tipo) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, categoria.getNome());
            stmt.setString(2, categoria.getTipo());
            stmt.executeUpdate();
            ResultSet chaves = stmt.getGeneratedKeys();
            if (chaves.next()) {
                categoria.setId(chaves.getInt(1));
            }
            return categoria;
        }
    }
    // lista todas as categorias existentes
    public List<Categoria> buscarTodas() throws SQLException {
        String sql = "SELECT id, nome, tipo FROM categorias ORDER BY nome";
        List<Categoria> lista = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(new Categoria(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("tipo")
                ));
            }
        }
        return lista;
    }

    // procurar Categoria por ID
    public Optional<Categoria> buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, nome, tipo FROM categorias WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(new Categoria(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("tipo")
                ));
            }
            return Optional.empty();
        }
    }

    // PROCURANDO PELO NOME PARA FILTRAR DUPLICADAS
    public Optional<Categoria> buscarPorNome(String nome) throws SQLException {
        String sql = "SELECT id, nome, tipo FROM categorias WHERE LOWER(nome) = LOWER(?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(new Categoria(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("tipo")
                ));
            }
            return Optional.empty();
        }
    }

    // ATUALIZANDO NOME E TIPO DA CATEGORIA
    public boolean atualizar(Categoria categoria) throws SQLException {
        String sql = "UPDATE categorias SET nome = ?, tipo = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, categoria.getNome());
            stmt.setString(2, categoria.getTipo());
            stmt.setInt(3, categoria.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    // EXCLUIR CATEGORIA POR ID
    public boolean excluir(int id) throws SQLException {
        String sql = "DELETE FROM categorias WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // VERFICAR SE EXISTEM TRANSAÇÕES COM AQUELA CATEGORIA PELO ID
    public boolean possuiTransacoesVinculadas(int categoriaId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM transacoes WHERE categoria_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoriaId);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        }
    }
}