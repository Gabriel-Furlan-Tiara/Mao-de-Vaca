package com.financeiro.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

// Serve a pagina HTML do frontend.
// Le o arquivo index.html que fica em src/main/resources.
public class FrontendHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        // so responde a pagina na raiz "/" ou "/index.html"
        if (path.equals("/") || path.equals("/index.html")) {

            // le o index.html de dentro do classpath (pasta resources)
            try (InputStream is = getClass().getResourceAsStream("/index.html")) {

                if (is == null) {
                    enviar(exchange, 500, "index.html nao encontrado em resources.");
                    return;
                }

                byte[] html = is.readAllBytes();
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, html.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(html);
                }
                exchange.close();
            }

        } else {
            enviar(exchange, 404, "Pagina nao encontrada.");
        }
    }

    private void enviar(HttpExchange exchange, int status, String texto) throws IOException {
        byte[] bytes = texto.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
        exchange.close();
    }
}
