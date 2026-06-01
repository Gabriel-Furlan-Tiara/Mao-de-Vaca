package com.financeiro.handler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financeiro.model.Transacao;
import com.financeiro.service.TransacaoService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
public class TransacaoHandler implements HttpHandler {
    private final TransacaoService service;
    private final ObjectMapper mapper = new ObjectMapper();
    public TransacaoHandler(TransacaoService service) {
        this.service = service;
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String metodo = exchange.getRequestMethod();
        String path   = exchange.getRequestURI().getPath();
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        try {
            if (metodo.equals("POST") && path.equals("/api/transacoes")) {
                Transacao transacao = lerCorpo(exchange, Transacao.class);
                Transacao salva = service.registrar(transacao);
                enviarResposta(exchange, 201, mapper.writeValueAsString(salva));
            } else if (metodo.equals("GET") && path.equals("/api/transacoes")) {
                List<Transacao> lista = service.listarTodas();
                enviarResposta(exchange, 200, mapper.writeValueAsString(lista));
            } else if (metodo.equals("PUT") && path.matches("/api/transacoes/\\d+")) {
                int id = extrairId(path);
                Transacao transacao = lerCorpo(exchange, Transacao.class);
                Transacao atualizada = service.editar(id, transacao);
                enviarResposta(exchange, 200, mapper.writeValueAsString(atualizada));
            } else if (metodo.equals("DELETE") && path.matches("/api/transacoes/\\d+")) {
                int id = extrairId(path);
                service.excluir(id);
                enviarResposta(exchange, 204, "");
            } else {
                enviarResposta(exchange, 404, erro("Rota não encontrada."));
            }
        } catch (IllegalArgumentException e) {
            enviarResposta(exchange, 400, erro(e.getMessage()));
        } catch (IllegalStateException e) {
            enviarResposta(exchange, 409, erro(e.getMessage()));
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            enviarResposta(exchange, 500, erro("Erro interno no servidor."));
        }
    }
    private <T> T lerCorpo(HttpExchange exchange, Class<T> tipo) throws IOException {
        InputStream corpo = exchange.getRequestBody();
        return mapper.readValue(corpo, tipo);
    }
    private int extrairId(String path) {
        String[] partes = path.split("/");
        return Integer.parseInt(partes[partes.length - 1]);
    }
    private String erro(String mensagem) {
        return "{\"erro\": \"" + mensagem + "\"}";
    }
    private void enviarResposta(HttpExchange exchange, int status, String corpo) throws IOException {
        byte[] bytes = corpo.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, corpo.isEmpty() ? -1 : bytes.length);
        if (!corpo.isEmpty()) {
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }
}