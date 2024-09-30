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

import static java.net.HttpURLConnection.*;

/**
 * Тест-класс для изменения данных пользователя Stellar Burgers:
 * - с авторизацией,
 * - без авторизации.
 */
public class ChangeUserDataTest {
    private UserClient userClient;

    @Before
    @Step("Предусловие: создание пользователя")
    public void setUp() {
        userClient = new UserClient();
        createUser(TestValue.TEST_LOGIN_ONE, TestValue.TEST_PASSWORD_ONE, TestValue.TEST_NAME_ONE);
    }

    @After
    @Step("Постусловие: удаление пользователя")
    public void clearData() {
        deleteUser(TestValue.TEST_LOGIN_TWO, TestValue.TEST_PASSWORD_TWO, TestValue.TEST_NAME_TWO);
    }

    @Test
    @DisplayName("Изменение информации о пользователе с авторизацией. Ожидаемый ответ: 200")
    @Description("Отправляем PATCH запрос на ручку /api/auth/user с авторизацией.")
    public void updateUserWithAuth() {
        UserStellar userStellar = new UserStellar(TestValue.TEST_LOGIN_ONE, TestValue.TEST_PASSWORD_ONE, TestValue.TEST_NAME_ONE);
        String accessToken = loginAndExtractToken(userStellar);

        UserStellar userStellarTwo = new UserStellar(TestValue.TEST_LOGIN_TWO, TestValue.TEST_PASSWORD_TWO, TestValue.TEST_NAME_TWO);
        updateUser(accessToken, userStellarTwo)
                .assertThat().statusCode(HTTP_OK);
    }

    @Test
    @DisplayName("Изменение информации о пользователе без авторизации. Ожидаемый ответ: 401")
    @Description("Отправляем PATCH запрос на ручку /api/auth/user без авторизации.")
    public void updateUserWithoutAuth() {
        UserStellar userStellar = new UserStellar(TestValue.TEST_LOGIN_ONE, TestValue.TEST_PASSWORD_ONE, TestValue.TEST_NAME_ONE);
        String accessToken = loginAndExtractToken(userStellar);

        UserStellar userStellarTwo = new UserStellar(TestValue.TEST_LOGIN_TWO, TestValue.TEST_PASSWORD_TWO, TestValue.TEST_NAME_TWO);
        userClient.updateUserNotAuth(userStellarTwo)
                .assertThat().statusCode(HTTP_UNAUTHORIZED);
        userClient.deleteUser(accessToken);
    }

    @Step("Создание пользователя с email: {0}, паролем: {1} и именем: {2}")
    private void createUser(String email, String password, String name) {
        UserStellar userStellar = new UserStellar(email, password, name);
        ValidatableResponse response = userClient.createUser(userStellar);
        response.assertThat().statusCode(HTTP_OK); // проверяем, что создание успешно
    }

    @Step("Удаление пользователя с email: {0}, паролем: {1} и именем: {2}")
    private void deleteUser(String email, String password, String name) {
        try {
            UserStellar userStellar = new UserStellar(email, password, name);
            ValidatableResponse responseLogin = userClient.loginUser(userStellar);
            String accessTokenWithBearer = responseLogin.extract().path("accessToken").toString();
            String accessToken = accessTokenWithBearer.replace("Bearer ", "");
            userClient.deleteUser(accessToken).assertThat().statusCode(HTTP_OK); // проверяем успешное удаление
            System.out.println("Пользователь успешно удален.");
        } catch (Exception e) {
            System.out.println("Пользователь не был удален через постусловие.");
        }
    }

    @Step("Авторизация пользователя и извлечение токена доступа")
    private String loginAndExtractToken(UserStellar userStellar) {
        ValidatableResponse responseLogin = userClient.loginUser(userStellar);
        String accessTokenWithBearer = responseLogin.extract().path("accessToken").toString();
        return accessTokenWithBearer.replace("Bearer ", "");
    }


    @Step("Обновление информации о пользователе с авторизацией")
    private ValidatableResponse updateUser(String accessToken, UserStellar userStellar) {
        return userClient.updateUser(accessToken, userStellar);
    }
}
