package order;

import io.restassured.response.ValidatableResponse;
import model.BaseReqSpecURI;
import model.OrderStellar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static io.restassured.RestAssured.given;

/**
 * Класс для работы с заказами Stellar Burgers через API.
 * <p>
 * Содержит методы для создания заказа с авторизацией и без, а также для получения заказов пользователя.
 */
public class OrderClient extends BaseReqSpecURI {

    /**
     * Создаёт заказ без авторизации.
     *
     * @param orderStellar Объект заказа с ингредиентами. Не может быть null.
     * @return Ответ API, который можно валидировать.
     */
    @NotNull
    public ValidatableResponse orderWithoutAuth(@NotNull OrderStellar orderStellar) {
        return given()
                .spec(getBaseReqSpec())
                .body(orderStellar)
                .post("/api/orders")
                .then();
    }

    /**
     * Создаёт заказ с авторизацией.
     *
     * @param accessToken   Токен доступа пользователя для авторизации. Не может быть null.
     * @param orderStellar  Объект заказа с ингредиентами. Не может быть null.
     * @return Ответ API, который можно валидировать.
     */
    @NotNull
    public ValidatableResponse orderWithAuth(@NotNull String accessToken, @NotNull OrderStellar orderStellar) {
        return given()
                .spec(getBaseReqSpec())
                .body(orderStellar)
                .auth().oauth2(accessToken)
                .post("/api/orders")
                .then();
    }

    /**
     * Получение заказов пользователя с авторизацией.
     *
     * @param accessToken Токен доступа пользователя для авторизации. Не может быть null.
     * @return Ответ API, который можно валидировать.
     */
    @NotNull
    public ValidatableResponse getOrderUserAuth(@NotNull String accessToken) {
        return given()
                .spec(getBaseReqSpec())
                .auth().oauth2(accessToken)
                .get("/api/orders")
                .then();
    }

    /**
     * Получение заказов пользователя без авторизации.
     *
     * @return Ответ API, который можно валидировать.
     */
    @NotNull
    public ValidatableResponse getOrderUserNotAuth() {
        return given()
                .spec(getBaseReqSpec())
                .get("/api/orders")
                .then();
    }
}
