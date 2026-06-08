package com.financeiro.service;
import com.financeiro.database.DatabaseConnection;
import com.financeiro.model.Categoria;
import com.financeiro.model.Transacao;
import com.financeiro.repository.CategoriaRepository;
import com.financeiro.repository.TransacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class TransacaoServiceTest {
    private TransacaoService service;
    private int categoriaIdValido; // guarda o ID de uma categoria real para usar nos testes

    // roda antes de CADA teste — prepara os services e cria uma categoria válida
    @BeforeEach
    void setUp() throws SQLException {
        DatabaseConnection.iniciarBanco();

        CategoriaRepository categoriaRepo = new CategoriaRepository();
        TransacaoRepository transacaoRepo = new TransacaoRepository();

        CategoriaService categoriaService = new CategoriaService(categoriaRepo);
        service = new TransacaoService(transacaoRepo, categoriaRepo);

        // cria uma categoria para vincular nas transações de teste
        Categoria cat = new Categoria();
        cat.setNome("CatTeste_" + System.currentTimeMillis());
        cat.setTipo("DESPESA");
        categoriaIdValido = categoriaService.criar(cat).getId();
    }

    // RN05 — valor igual a zero não é permitido
    @Test
    @DisplayName("RN05 - Valor zero deve lançar exceção")
    void valorZeroDeveLancarExcecao() {
        Transacao t = criarTransacaoValida();
        t.setValor(0);

        assertThrows(IllegalArgumentException.class, () -> service.registrar(t));
    }

    // RN05 — valor negativo não é permitido
    @Test
    @DisplayName("RN05 - Valor negativo deve lançar exceção")
    void valorNegativoDeveLancarExcecao() {
        Transacao t = criarTransacaoValida();
        t.setValor(-100);

        assertThrows(IllegalArgumentException.class, () -> service.registrar(t));
    }

    // RN06 — não pode registrar transação com data no futuro
    @Test
    @DisplayName("RN06 - Data futura deve lançar exceção")
    void dataFuturaDeveLancarExcecao() {
        Transacao t = criarTransacaoValida();
        t.setData(LocalDate.now().plusDays(1).toString()); // amanhã

        assertThrows(IllegalArgumentException.class, () -> service.registrar(t));
    }

    // RN07 — a categoria informada precisa existir no banco
    @Test
    @DisplayName("RN07 - Categoria inexistente deve lançar exceção")
    void categoriaInexistenteDeveLancarExcecao() {
        Transacao t = criarTransacaoValida();
        t.setCategoriaId(999999); // ID que não existe

        assertThrows(IllegalArgumentException.class, () -> service.registrar(t));
    }

    // RN04 — tipo deve ser RECEITA ou DESPESA
    @Test
    @DisplayName("RN04 - Tipo inválido deve lançar exceção")
    void tipoInvalidoDeveLancarExcecao() {
        Transacao t = criarTransacaoValida();
        t.setTipo("OUTRO");

        assertThrows(IllegalArgumentException.class, () -> service.registrar(t));
    }

    // fluxo feliz — registra uma despesa válida com sucesso
    @Test
    @DisplayName("Deve registrar despesa com sucesso")
    void deveRegistrarDespesaComSucesso() throws SQLException {
        Transacao t = criarTransacaoValida();

        Transacao salva = service.registrar(t);

        assertTrue(salva.getId() > 0);            // banco gerou o ID
        assertEquals("DESPESA", salva.getTipo());
        assertEquals(150.0, salva.getValor());
    }

    // métod o auxiliar — cria uma transação com todos os campos válidos
    // assim cada teste só altera o campo que quer testar
    private Transacao criarTransacaoValida() {
        Transacao t = new Transacao();
        t.setTipo("DESPESA");
        t.setValor(150.0);
        t.setData(LocalDate.now().toString());
        t.setDescricao("Compra de teste");
        t.setCategoriaId(categoriaIdValido);
        return t;
    }
}
