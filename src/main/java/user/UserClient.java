package user;

import io.restassured.response.ValidatableResponse;
import model.BaseReqSpecURI;
import model.UserStellar;
import org.jetbrains.annotations.NotNull;

import static io.restassured.RestAssured.given;

/**
 * Класс для работы с API пользователя Stellar Burgers.
 * <p>
 * Содержит методы для регистрации, авторизации, обновления и удаления пользователей.
 */
public class UserClient extends BaseReqSpecURI {

    /**
     * Создаёт нового пользователя.
     *
     * @param userStellar Объект пользователя для регистрации. Не может быть null.
     * @return Ответ API, который можно валидировать.
     */
    @NotNull
    public ValidatableResponse createUser(@NotNull UserStellar userStellar) {
        return given()
                .spec(getBaseReqSpec())
                .body(userStellar)
                .when()
                .post("/api/auth/register")
                .then();
    }

    /**
     * Авторизует пользователя.
     *
     * @param userStellar Объект пользователя для авторизации (логин и пароль). Не может быть null.
     * @return Ответ API, который можно валидировать.
     */
    @NotNull
    public ValidatableResponse loginUser(@NotNull UserStellar userStellar) {
        return given()
                .spec(getBaseReqSpec())
                .body(userStellar)
                .when()
                .post("/api/auth/login")
                .then();
    }

    /**
     * Удаляет пользователя (требуется авторизация).
     *
     * @param accessToken Токен доступа пользователя для авторизации. Не может быть null.
     * @return Ответ API, который можно валидировать.
     */
    @NotNull
    public ValidatableResponse deleteUser(@NotNull String accessToken) {
        return given()
                .spec(getBaseReqSpec())
                .auth().oauth2(accessToken)
                .delete("/api/auth/user")
                .then();
    }

    /**
     * Обновляет данные пользователя (с авторизацией).
     *
     * @param accessToken  Токен доступа пользователя для авторизации. Не может быть null.
     * @param userStellar  Объект с обновлёнными данными пользователя. Не может быть null.
     * @return Ответ API, который можно валидировать.
     */
    @NotNull
    public ValidatableResponse updateUser(@NotNull String accessToken, @NotNull UserStellar userStellar) {
        return given()
                .spec(getBaseReqSpec())
                .body(userStellar)
                .auth().oauth2(accessToken)
                .patch("/api/auth/user")
                .then();
    }

    /**
     * Обновляет данные пользователя без авторизации.
     *
     * @param userStellar Объект с обновлёнными данными пользователя. Не может быть null.
     * @return Ответ API, который можно валидировать.
     */
    @NotNull
    public ValidatableResponse updateUserNotAuth(@NotNull UserStellar userStellar) {
        return given()
                .spec(getBaseReqSpec())
                .body(userStellar)
                .patch("/api/auth/user")
                .then();
    }
}
