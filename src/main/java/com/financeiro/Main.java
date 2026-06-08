package com.financeiro;
import com.financeiro.database.DatabaseConnection;
import com.financeiro.handler.CategoriaHandler;
import com.financeiro.handler.FrontendHandler;
import com.financeiro.handler.RelatorioHandler;
import com.financeiro.handler.TransacaoHandler;
import com.financeiro.repository.CategoriaRepository;
import com.financeiro.repository.TransacaoRepository;
import com.financeiro.service.CategoriaService;
import com.financeiro.service.RelatorioService;
import com.financeiro.service.TransacaoService;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws IOException {

        // 1. Inicializa o banco
        DatabaseConnection.iniciarBanco();

        // 2. Repositories
        CategoriaRepository categoriaRepo = new CategoriaRepository();
        TransacaoRepository transacaoRepo  = new TransacaoRepository();

        // 3. Services
        CategoriaService categoriaService = new CategoriaService(categoriaRepo);
        TransacaoService transacaoService  = new TransacaoService(transacaoRepo, categoriaRepo);
        RelatorioService relatorioService  = new RelatorioService(transacaoRepo, categoriaRepo);

        // 4. Handlers
        CategoriaHandler categoriaHandler = new CategoriaHandler(categoriaService);
        TransacaoHandler transacaoHandler  = new TransacaoHandler(transacaoService);
        RelatorioHandler relatorioHandler  = new RelatorioHandler(relatorioService);
        FrontendHandler frontendHandler    = new FrontendHandler();

        // 5. Servidor HTTP
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/categorias", categoriaHandler);
        server.createContext("/api/transacoes",  transacaoHandler);
        server.createContext("/api/relatorios",  relatorioHandler);
        server.createContext("/", frontendHandler); // serve a pagina HTML

        server.start();
        System.out.println("========================================");
        System.out.println("  Mao de Vaca rodando!");
        System.out.println("  Abra no navegador: http://localhost:8080");
        System.out.println("========================================");
    }
}

