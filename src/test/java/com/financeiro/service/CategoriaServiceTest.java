package com.financeiro.service;
import com.financeiro.database.DatabaseConnection;
import com.financeiro.model.Categoria;
import com.financeiro.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

class CategoriaServiceTest {
    private CategoriaService service;

    // roda antes de CADA teste — garante que o banco existe e cria um service novo
    @BeforeEach
    void setUp() {
        DatabaseConnection.iniciarBanco();
        service = new CategoriaService(new CategoriaRepository());
    }

    // RN01 — não deve aceitar categoria com nome vazio
    @Test
    @DisplayName("RN01 - Nome vazio deve lançar exceção")
    void nomeVazioDeveLancarExcecao() {
        Categoria categoria = new Categoria();
        categoria.setNome("");
        categoria.setTipo("DESPESA");

        assertThrows(IllegalArgumentException.class, () -> service.criar(categoria));
    }

    // RN01 — não deve aceitar categoria com nome nulo
    @Test
    @DisplayName("RN01 - Nome nulo deve lançar exceção")
    void nomeNuloDeveLancarExcecao() {
        Categoria categoria = new Categoria();
        categoria.setNome(null);
        categoria.setTipo("DESPESA");

        assertThrows(IllegalArgumentException.class, () -> service.criar(categoria));
    }

    // RN01 — não deve aceitar tipo fora dos valores permitidos
    @Test
    @DisplayName("RN01 - Tipo inválido deve lançar exceção")
    void tipoInvalidoDeveLancarExcecao() {
        Categoria categoria = new Categoria();
        categoria.setNome("Teste");
        categoria.setTipo("QUALQUERCOISA");

        assertThrows(IllegalArgumentException.class, () -> service.criar(categoria));
    }

    // RN02 — não deve permitir duas categorias com o mesmo nome
    @Test
    @DisplayName("RN02 - Nome duplicado deve lançar exceção")
    void nomeDuplicadoDeveLancarExcecao() throws SQLException {
        // cria a primeira categoria (nome único usando o tempo atual)
        Categoria c1 = new Categoria();
        c1.setNome("Duplicada_" + System.currentTimeMillis());
        c1.setTipo("DESPESA");
        service.criar(c1);

        // tenta criar outra com o mesmo nome
        Categoria c2 = new Categoria();
        c2.setNome(c1.getNome());
        c2.setTipo("RECEITA");

        assertThrows(IllegalStateException.class, () -> service.criar(c2));
    }

    // fluxo feliz — deve criar a categoria e o banco deve gerar um ID
    @Test
    @DisplayName("Deve criar categoria com sucesso")
    void deveCriarCategoriaComSucesso() throws SQLException {
        Categoria categoria = new Categoria();
        categoria.setNome("Salario_" + System.currentTimeMillis());
        categoria.setTipo("RECEITA");

        Categoria criada = service.criar(categoria);

        assertTrue(criada.getId() > 0);          // o banco gerou um ID válido
        assertEquals("RECEITA", criada.getTipo()); // o tipo foi salvo corretamente
    }
}
