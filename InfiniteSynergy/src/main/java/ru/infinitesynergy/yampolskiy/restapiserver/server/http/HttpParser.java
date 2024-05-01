package ru.infinitesynergy.yampolskiy.restapiserver.server.http;


import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class HttpParser {


    public static HttpRequest parseRawHttp(String rawHttp) {
        HttpRequest httpRequest = new HttpRequest();
        String requestLine = getRequestLine(rawHttp);
        parseRequestLine(requestLine, httpRequest);
        httpRequest.setHeaders(getHeaders(rawHttp));
        httpRequest.setBody(getBody(rawHttp));
        return httpRequest;
    }

    private static String getRequestLine(String rawHttp) {
        // Разделение запроса на строки
        String[] requestLines = rawHttp.split("\r\n");

        // Первая строка запроса - стартовая строка
        return requestLines[0];
    }


    private static void parseRequestLine(String requestLines, HttpRequest httpRequest) {

        int methodEndIndex = requestLines.indexOf(' '); // Индекс конца метода запроса
        String method = requestLines.substring(0, methodEndIndex); // Метод запроса
        httpRequest.setMethod(HttpMethod.valueOf(method));

        int uriStartIndex = methodEndIndex + 1;
        int uriEndIndex = requestLines.indexOf(' ', uriStartIndex);
        String uri = requestLines.substring(uriStartIndex, uriEndIndex); // адрес запроса
        httpRequest.setUri(URI.create(uri));
        if (uri.contains("?")) {
            String[] elements = uri.split("[?]");
            httpRequest.setUri(URI.create(elements[0]));
            String[] keysValues = elements[1].split("&");
            for (String o : keysValues) {
                String[] keyValue = o.split("=");
                httpRequest.addRequestParameter(keyValue[0], keyValue[1]);
            }
        }

        int protocolStartIndex = uriEndIndex + 1; // Индекс начала протокола и его версии
        String protocolAndVersion = requestLines.substring(protocolStartIndex); // Протокол и его версия
        httpRequest.setProtocolVersion(protocolAndVersion);
    }


    private static Map<String, String> getHeaders(String rawHttp) {
        Map<String, String> headers = new HashMap<>();

        // Разделение запроса на строки
        String[] requestLines = rawHttp.split("\r\n");

        // Находим индекс первой пустой строки
        int emptyLineIndex = -1;
        for (int i = 0; i < requestLines.length; i++) {
            if (requestLines[i].isEmpty()) {
                emptyLineIndex = i;
                break;
            }
        }

        // Если нет пустой строки, заголовков нет
        if (emptyLineIndex == -1) {
            return headers;
        }

        // Парсим заголовки
        for (int i = 1; i < emptyLineIndex; i++) {
            String[] headerParts = requestLines[i].split(": ", 2);
            if (headerParts.length == 2) {
                headers.put(headerParts[0], headerParts[1]);
            }
        }

        return headers;
    }

    private static String getBody(String rawHttp) {
        // Разделение запроса на строки
        String[] requestLines = rawHttp.split("\r\n");

        // Находим индекс первой пустой строки
        int emptyLineIndex = -1;
        for (int i = 0; i < requestLines.length; i++) {
            if (requestLines[i].isEmpty()) {
                emptyLineIndex = i;
                break;
            }
        }

        // Если нет пустой строки, тела нет
        if (emptyLineIndex == -1 || emptyLineIndex == requestLines.length - 1) {
            return null;
        }

        // Сборка тела запроса
        StringBuilder bodyBuilder = new StringBuilder();
        for (int i = emptyLineIndex + 1; i < requestLines.length; i++) {
            bodyBuilder.append(requestLines[i]);
            if (i < requestLines.length - 1) {
                bodyBuilder.append("\r\n");
            }
        }

        return bodyBuilder.toString();
    }

}
