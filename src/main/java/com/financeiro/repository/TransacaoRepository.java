package com.financeiro.repository;
import com.financeiro.database.DatabaseConnection;
import com.financeiro.model.Transacao;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
public class TransacaoRepository {
    public Transacao salvar(Transacao transacao) throws SQLException {
        String sql = "INSERT INTO transacoes (tipo, valor, data, descricao, categoria_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, transacao.getTipo());
            stmt.setDouble(2, transacao.getValor());
            stmt.setDate(3, Date.valueOf(transacao.getData()));
            stmt.setString(4, transacao.getDescricao());
            stmt.setInt(5, transacao.getCategoriaId());
            stmt.executeUpdate();
            ResultSet chaves = stmt.getGeneratedKeys();
            if (chaves.next()) {
                transacao.setId(chaves.getInt(1));
            }
            return transacao;
        }
    }

    public List<Transacao> buscarTodas() throws SQLException {
        String sql = "SELECT id, tipo, valor, data, descricao, categoria_id FROM transacoes ORDER BY data DESC";
        List<Transacao> lista = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Optional<Transacao> buscarPorId(int id) throws SQLException {
        String sql = "SELECT id, tipo, valor, data, descricao, categoria_id FROM transacoes WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapear(rs));
            }
            return Optional.empty();
        }
    }

    public boolean atualizar(Transacao transacao) throws SQLException {
        String sql = "UPDATE transacoes SET tipo = ?, valor = ?, data = ?, descricao = ?, categoria_id = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, transacao.getTipo());
            stmt.setDouble(2, transacao.getValor());
            stmt.setDate(3, Date.valueOf(transacao.getData()));
            stmt.setString(4, transacao.getDescricao());
            stmt.setInt(5, transacao.getCategoriaId());
            stmt.setInt(6, transacao.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean excluir(int id) throws SQLException {
        String sql = "DELETE FROM transacoes WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // converte uma linha do banco em objeto Transacao
    private Transacao mapear(ResultSet rs) throws SQLException {
        return new Transacao(
                rs.getInt("id"),
                rs.getString("tipo"),
                rs.getDouble("valor"),
                rs.getDate("data").toString(),
                rs.getString("descricao"),
                rs.getInt("categoria_id")
        );
    }
}