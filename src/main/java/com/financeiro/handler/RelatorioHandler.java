package com.financeiro.handler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financeiro.model.RelatorioMensal;
import com.financeiro.service.RelatorioService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

// Handler do relatorio mensal (UC04).
// Responde a GET /api/relatorios?mes=MM&ano=AAAA devolvendo um JSON.
public class RelatorioHandler implements HttpHandler {
    private final RelatorioService service;
    private final ObjectMapper mapper = new ObjectMapper();

    public RelatorioHandler(RelatorioService service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String metodo = exchange.getRequestMethod();
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        try {
            if (metodo.equals("GET")) {
                // le os parametros da query string (?mes=5&ano=2026)
                Map<String, String> params = lerParametros(exchange.getRequestURI().getQuery());
                if (!params.containsKey("mes") || !params.containsKey("ano")) {
                    enviarResposta(exchange, 400, erro("Informe os parametros mes e ano."));
                    return;
                }
                int mes = Integer.parseInt(params.get("mes"));
                int ano = Integer.parseInt(params.get("ano"));
                RelatorioMensal relatorio = service.gerarDados(mes, ano);
                enviarResposta(exchange, 200, mapper.writeValueAsString(relatorio));
            } else {
                enviarResposta(exchange, 404, erro("Rota nao encontrada."));
            }
        } catch (NumberFormatException e) {
            enviarResposta(exchange, 400, erro("Mes e ano devem ser numeros."));
        } catch (IllegalArgumentException e) {
            enviarResposta(exchange, 400, erro(e.getMessage()));
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            enviarResposta(exchange, 500, erro("Erro interno no servidor."));
        }
    }

    // transforma "mes=5&ano=2026" em um mapa { mes:5, ano:2026 }
    private Map<String, String> lerParametros(String query) {
        Map<String, String> mapa = new HashMap<>();
        if (query == null) return mapa;
        for (String par : query.split("&")) {
            String[] partes = par.split("=");
            if (partes.length == 2) {
                mapa.put(partes[0], partes[1]);
            }
        }
        return mapa;
    }

    private String erro(String mensagem) {
        return "{\"erro\": \"" + mensagem + "\"}";
    }

    private void enviarResposta(HttpExchange exchange, int status, String corpo) throws IOException {
        byte[] bytes = corpo.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
        exchange.close();
    }
}d