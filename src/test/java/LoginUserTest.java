import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.UserStellar;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testValue.TestValue;
import user.UserClient;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.*;

/**
 * Тесты для логина пользователя:
 * - логин с существующим пользователем,
 * - логин с неверным логином и паролем.
 */
public class LoginUserTest {
    private UserClient userClient;
    private ValidatableResponse responseLogin;
    private UserStellar userStellar;

    @Before
    @Step("Предусловие: создание пользователя")
    public void setUp() {
        userClient = new UserClient();
        userStellar = new UserStellar(TestValue.TEST_LOGIN_ONE, TestValue.TEST_PASSWORD_ONE, TestValue.TEST_NAME_ONE);
        responseLogin = userClient.createUser(userStellar);
    }

    @Test
    @DisplayName("Логин под существующим пользователем. Ожидаемый ответ: 200")
    @Description("Post запрос на ручку /api/auth/login для логина с существующим пользователем.")
    public void loginWithUserTrueAndCheckBody() {
        performLoginAndCheckResponse(responseLogin);
    }

    @Test
    @DisplayName("Логин с неверным email. Ожидаемый ответ: 401")
    @Description("Post запрос на ручку /api/auth/login для логина с неверным email.")
    public void loginWithUserFalseAndCheckBody() {
        userStellar.setEmail(TestValue.TEST_LOGIN_TWO);
        ValidatableResponse response = userClient.loginUser(userStellar)
                .assertThat().statusCode(HTTP_UNAUTHORIZED);
        checkInvalidLoginResponse(response);
    }

    @Test
    @DisplayName("Логин с неверным паролем. Ожидаемый ответ: 401")
    @Description("Post запрос на ручку /api/auth/login для логина с неверным паролем.")
    public void loginWithUserFalsePasswordAndCheckBody() {
        userStellar.setPassword(TestValue.TEST_PASSWORD_TWO);
        ValidatableResponse response = userClient.loginUser(userStellar)
                .assertThat().statusCode(HTTP_UNAUTHORIZED);
        checkInvalidLoginResponse(response);
    }

    @After
    @Step("Постусловие: удаление пользователя")
    public void clearData() {
        try {
            String accessTokenWithBearer = responseLogin.extract().path("accessToken");
            String accessToken = accessTokenWithBearer.replace("Bearer ", "");
            userClient.deleteUser(accessToken);
            System.out.println("Пользователь успешно удален.");
        } catch (Exception e) {
            System.out.println("Пользователь не удалился. Возможно ошибка при создании.");
        }
    }

    @Step("Проверка успешного логина пользователя")
    private void performLoginAndCheckResponse(ValidatableResponse response) {
        response.assertThat().statusCode(HTTP_OK);
        response.assertThat().body("success", equalTo(true));
        response.assertThat().body("accessToken", startsWith("Bearer "))
                .and().body("refreshToken", notNullValue());
        response.assertThat().body("user.email", equalTo(TestValue.TEST_LOGIN_ONE))
                .and().body("user.name", equalTo(TestValue.TEST_NAME_ONE));
    }

    @Step("Проверка неуспешного логина (неверный email или пароль)")
    private void checkInvalidLoginResponse(ValidatableResponse response) {
        response.assertThat().body("success", equalTo(false))
                .and().body("message", equalTo("email or password are incorrect"));
    }
}
