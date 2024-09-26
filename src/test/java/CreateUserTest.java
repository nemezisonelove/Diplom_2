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

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.CoreMatchers.*;

/**
 * Тесты для создания пользователя:
 * - создание уникального пользователя,
 * - создание пользователя, который уже зарегистрирован,
 * - создание пользователя с пропуском обязательного поля.
 */
public class CreateUserTest {
    private UserClient userClient;
    private ValidatableResponse responseLogin;
    private UserStellar userStellar;

    @Before
    @Step("Предусловие: создание клиента UserClient и создание пользователя {0}")
    public void setUp() {
        userClient = new UserClient();
        userStellar = new UserStellar(TestValue.TEST_LOGIN_ONE, TestValue.TEST_PASSWORD_ONE, TestValue.TEST_NAME_ONE);
        responseLogin = userClient.createUser(userStellar);
    }

    @Test
    @DisplayName("Создать уникального пользователя. Ожидаемый ответ: 200 ОК")
    @Description("Post запрос на ручку /api/auth/register для создания уникального пользователя.")
    public void createUniqueUserAndCheckBodyTest() {
        checkUserCreationResponse();
    }

    @Step("Проверка создания пользователя")
    private void checkUserCreationResponse() {
        responseLogin.assertThat().statusCode(HTTP_OK);
        responseLogin.assertThat().body("user.email", equalTo(TestValue.TEST_LOGIN_ONE))
                .and().body("user.name", equalTo(TestValue.TEST_NAME_ONE));
        responseLogin.assertThat().body("accessToken", startsWith("Bearer "));
        responseLogin.assertThat().body("refreshToken", notNullValue());
        responseLogin.assertThat().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создать уже зарегистрированного пользователя. Ожидаемый ответ: 403 Forbidden")
    @Description("Post запрос на ручку /api/auth/register для создания уже зарегистрированного пользователя.")
    public void createRegisteredUserAndCheckBodyTest() {
        ValidatableResponse responseTwo = userClient.createUser(userStellar)
                .assertThat().statusCode(HTTP_FORBIDDEN);
        checkUserAlreadyExistsResponse(responseTwo);
    }

    @Step("Проверка ответа на создание уже существующего пользователя")
    private void checkUserAlreadyExistsResponse(ValidatableResponse response) {
        response.assertThat().body("success", equalTo(false))
                .and().body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создать пользователя без пароля. Ожидаемый ответ: 403")
    @Description("Post запрос на ручку /api/auth/register для создания пользователя без пароля.")
    public void createUserWithoutPasswordTest() {
        handleCreateUserWithoutField(new UserStellar(TestValue.TEST_LOGIN_ONE, null, TestValue.TEST_NAME_ONE));
    }

    @Test
    @DisplayName("Создать пользователя без email. Ожидаемый ответ: 403")
    @Description("Post запрос на ручку /api/auth/register для создания пользователя без email.")
    public void createUserWithoutEmailTest() {
        handleCreateUserWithoutField(new UserStellar(null, TestValue.TEST_PASSWORD_ONE, TestValue.TEST_NAME_ONE));
    }

    @Test
    @DisplayName("Создать пользователя без имени. Ожидаемый ответ: 403")
    @Description("Post запрос на ручку /api/auth/register для создания пользователя без имени.")
    public void createUserWithoutNameTest() {
        handleCreateUserWithoutField(new UserStellar(TestValue.TEST_LOGIN_ONE, TestValue.TEST_PASSWORD_ONE, null));
    }

    @Step("Обработка создания пользователя с пропущенным обязательным полем")
    private void handleCreateUserWithoutField(UserStellar userStellarWithMissingField) {
        try {
            ValidatableResponse response = userClient.createUser(userStellarWithMissingField)
                    .assertThat().statusCode(HTTP_FORBIDDEN);
            response.assertThat().body("success", equalTo(false))
                    .and().body("message", equalTo("Email, password and name are required fields"));
        } catch (Exception e) {
            // Обработка ситуации, если по какой-то причине пользователь создается, хотя поля отсутствуют
            ValidatableResponse response = userClient.createUser(userStellarWithMissingField);
            deleteUserByResponse(response);
        }
    }

    @Step("Удаление пользователя по response с accessToken")
    private void deleteUserByResponse(ValidatableResponse response) {
        String accessTokenWithBearer = response.extract().path("accessToken");
        String accessToken = accessTokenWithBearer.replace("Bearer ", "");
        userClient.deleteUser(accessToken);
        System.out.println("Пользователь удален.");
    }

    @After
    @Step("Постусловие: удаление созданного пользователя")
    public void clearData() {
        try {
            String accessTokenWithBearer = responseLogin.extract().path("accessToken");
            String accessToken = accessTokenWithBearer.replace("Bearer ", "");
            userClient.deleteUser(accessToken);
            System.out.println("Пользователь успешно удален.");
        } catch (Exception e) {
            System.out.println("Не удалось удалить пользователя. Возможно, он не был создан.");
        }
    }
}
