package ru.infinitesynergy.yampolskiy.restapiserver.server.route;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.infinitesynergy.yampolskiy.restapiserver.entities.User;
import ru.infinitesynergy.yampolskiy.restapiserver.exceptions.NotValidMethodException;
import ru.infinitesynergy.yampolskiy.restapiserver.exceptions.UserNotFoundException;
import ru.infinitesynergy.yampolskiy.restapiserver.server.http.BearerAuthentication;
import ru.infinitesynergy.yampolskiy.restapiserver.utils.JwtUtils;
import ru.infinitesynergy.yampolskiy.restapiserver.utils.ObjectMapperSingleton;
import ru.infinitesynergy.yampolskiy.restapiserver.server.http.*;
import ru.infinitesynergy.yampolskiy.restapiserver.service.UserService;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


public class SingInRoute implements Route{

    private static final Logger logger = LogManager.getLogger(SingInRoute.class);
    private final UserService userService;

    public SingInRoute(UserService userService) {
        this.userService = userService;
    }

    @Override
    public HttpResponse execute(HttpRequest httpRequest) throws Exception {
        if (!httpRequest.getMethod().equals(HttpMethod.POST)) {
            throw new NotValidMethodException("Некорректный метод запроса: " + httpRequest.getMethod());
        }
        String stringUserDTO = httpRequest.getBody();
        User user = ObjectMapperSingleton.getInstance().readValue(stringUserDTO, User.class);
        User existUser = userService.getUserByUserName(user.getLogin());
        if (existUser == null || !existUser.getPassword().equals(user.getPassword())) {
            throw new UserNotFoundException("Неверно указан логин или пароль.");
        }
        String jwtToken = JwtUtils.createToken(user.getLogin());
        BearerAuthentication bearerAuth = new BearerAuthentication(jwtToken);
        logger.info("Пользователь {} авторизовался", user.getLogin());
        return new HttpResponse.Builder()
                .setProtocolVersion(httpRequest.getProtocolVersion())
                .setStatus(HttpStatus.OK)
                .addHeader(HttpHeader.DATE, ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME))
                .addHeader(HttpHeader.SERVER, "BankServer/0.1")
                .addHeader(HttpHeader.CONTENT_TYPE, "application/octet-stream")
                .addHeader(HttpHeader.AUTHORIZATION, bearerAuth.getJwtToken())
                .setBody("")
                .build();
    }
}
