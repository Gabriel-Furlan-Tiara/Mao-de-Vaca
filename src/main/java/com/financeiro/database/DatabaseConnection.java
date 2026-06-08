package com.financeiro.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:h2:./financeiro"; //URL do banco do Mão de Vaca
    private static final String USER = "sa"; //usuário ADMIN, padrão
    private static final String PASSWORD = ""; //sem senha para facilitar

    public static Connection getConnection() throws SQLException { //criando conexão para criar tabelas com SQL
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void iniciarBanco() {
        //CRIANDO TABELA DE CATEGORIAS
        //ID PARA FACILITAR IDENTIFICAÇÃO E INTEGRAÇÃO
        //NOME E TIPO PARA CATEGORIZAR NO PRODUTO FINAL
        String criarTabelaDeCategorias =
                """
                CREATE TABLE IF NOT EXISTS CATEGORIAS (
                id   INTEGER PRIMARY KEY AUTO_INCREMENT,
                nome VARCHAR(100) NOT NULL UNIQUE,
                tipo VARCHAR(50)  NOT NULL
                )
                """;

        String criarTabelaDeTransacoes=
                //CRIANDO TABELA DE TRANSAÇÕES
                //DATA, DESCRIÇÃO E VALOR PARA MANTER O CONTROLE DE QUANDO E OQUE FOI GASTO / RECEBIDO
                //ID, TIPO E CATEGORIA_ID PARA MANTER O CONTROLE
                """
               CREATE TABLE IF NOT EXISTS TRANSACOES (
               id           INTEGER PRIMARY KEY AUTO_INCREMENT,
               tipo         VARCHAR(50)    NOT NULL,
               valor        DECIMAL(15,2)  NOT NULL,
               data         DATE           NOT NULL,
               descricao    VARCHAR(255)   NOT NULL,
               categoria_id INTEGER        NOT NULL,
               FOREIGN KEY (categoria_id) REFERENCES CATEGORIAS(id)
               )
               """;

        //CRIANDO TENTATIVA DE CONEXÃO COM BANCO
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            try {
                stmt.execute(criarTabelaDeCategorias);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            try {
                stmt.execute(criarTabelaDeTransacoes);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Banco de Dados iniciado com sucesso ^_^!");
        } catch (SQLException e) {
            System.out.println("Erro ao iniciar Banco de Dados X﹏X!");
            throw new RuntimeException(e);
        }

    }
}
