package ru.infinitesynergy.yampolskiy.restapiserver.server.route;

import ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpRequest;
import ru.infinitesynergy.yampolskiy.restapiserver.server.http.HttpResponse;

public interface Route {
   HttpResponse execute(HttpRequest httpRequest) throws Exception;
}
