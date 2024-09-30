import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.OrderStellar;
import model.UserStellar;
import order.OrderClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testValue.TestValue;
import user.UserClient;

import java.util.ArrayList;

import static java.net.HttpURLConnection.*;
import static org.hamcrest.CoreMatchers.equalTo;

/**
 * Тесты для создания заказа через API Stellar Burgers:
 * - с авторизацией,
 * - без авторизации,
 * - с ингредиентами,
 * - без ингредиентов,
 * - с неверным хешем ингредиентов.
 */
public class CreateOrderTest {
    private UserClient userClient;
    private OrderClient orderClient;

    @Before
    @Step("Подготовка окружения: создание клиента для пользователя и заказов")
    public void setUp() {
        userClient = new UserClient();
        orderClient = new OrderClient();
    }

    @Test
    @DisplayName("Создание заказа без авторизации. Ожидаемый ответ: 200")
    @Description("Post запрос на ручку /api/orders без авторизации.")
    @Step("Создание заказа без авторизации")
    public void createOrderWithoutAuth() {
        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add(TestValue.TEST_BUN);
        ingredients.add(TestValue.TEST_FILLING_ONE);
        ingredients.add(TestValue.TEST_FILLING_TWO);
        OrderStellar orderStellar = new OrderStellar(ingredients);
        ValidatableResponse response = orderClient.orderWithoutAuth(orderStellar)
                .assertThat().statusCode(HTTP_OK);
    }

    @Test
    @DisplayName("Создание заказа без авторизации с неверным хешем ингредиента. Ожидаемый ответ: 500")
    @Description("Post запрос на ручку /api/orders с неверным хешем ингредиента.")
    @Step("Создание заказа с неверным хешем ингредиента без авторизации")
    public void createOrderWithoutAuthErrorHash() {
        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add(TestValue.TEST_BAD_BUN);
        ingredients.add(TestValue.TEST_FILLING_ONE);
        OrderStellar orderStellar = new OrderStellar(ingredients);
        ValidatableResponse response = orderClient.orderWithoutAuth(orderStellar)
                .assertThat().statusCode(HTTP_INTERNAL_ERROR);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов без авторизации. Ожидаемый ответ: 400")
    @Description("Post запрос на ручку /api/orders без ингредиентов.")
    @Step("Создание заказа без ингредиентов без авторизации")
    public void createOrderWithoutAuthNoIngredient() {
        // Передаем пустой список вместо null
        OrderStellar orderStellar = new OrderStellar(new ArrayList<>());
        ValidatableResponse response = orderClient.orderWithoutAuth(orderStellar)
                .assertThat().statusCode(HTTP_BAD_REQUEST);
        response.assertThat().body("success", equalTo(false))
                .and()
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с авторизацией. Ожидаемый ответ: 200")
    @Description("Post запрос на ручку /api/orders с авторизацией.")
    @Step("Создание заказа с авторизацией")
    public void createOrderWithAuth() {
        UserStellar userStellar = new UserStellar(TestValue.TEST_LOGIN_ONE, TestValue.TEST_PASSWORD_ONE, TestValue.TEST_NAME_ONE);
        ValidatableResponse responseCreate = userClient.createUser(userStellar).assertThat().statusCode(HTTP_OK);
        String accessTokenWithBearer = responseCreate.extract().path("accessToken");
        String accessToken = accessTokenWithBearer.replace("Bearer ", "");

        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add(TestValue.TEST_BUN);
        ingredients.add(TestValue.TEST_FILLING_ONE);
        ingredients.add(TestValue.TEST_FILLING_TWO);
        OrderStellar orderStellar = new OrderStellar(ingredients);
        ValidatableResponse response = orderClient.orderWithAuth(accessToken, orderStellar)
                .assertThat().statusCode(HTTP_OK);
        response.assertThat().body("order.owner.name", equalTo(TestValue.TEST_NAME_ONE))
                .and()
                .body("order.owner.email", equalTo(TestValue.TEST_LOGIN_ONE));
    }

    @Test
    @DisplayName("Создание заказа с авторизацией, но без ингредиентов. Ожидаемый ответ: 400")
    @Description("Post запрос на ручку /api/orders с авторизацией, но без ингредиентов.")
    @Step("Создание заказа с авторизацией, но без ингредиентов")
    public void createOrderWithAuthNoIngredient() {
        UserStellar userStellar = new UserStellar(TestValue.TEST_LOGIN_ONE, TestValue.TEST_PASSWORD_ONE, TestValue.TEST_NAME_ONE);
        ValidatableResponse responseCreate = userClient.createUser(userStellar).assertThat().statusCode(HTTP_OK);
        String accessTokenWithBearer = responseCreate.extract().path("accessToken");
        String accessToken = accessTokenWithBearer.replace("Bearer ", "");

        // Передаем пустой список вместо null
        OrderStellar orderStellar = new OrderStellar(new ArrayList<>());
        ValidatableResponse response = orderClient.orderWithAuth(accessToken, orderStellar)
                .assertThat().statusCode(HTTP_BAD_REQUEST);
        response.assertThat().body("success", equalTo(false))
                .and()
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиента и авторизацией. Ожидаемый ответ: 500")
    @Description("Post запрос на ручку /api/orders с неверным хешем ингредиента и авторизацией.")
    @Step("Создание заказа с неверным хешем ингредиента и авторизацией")
    public void createOrderWithAuthErrorHash() {
        UserStellar userStellar = new UserStellar(TestValue.TEST_LOGIN_ONE, TestValue.TEST_PASSWORD_ONE, TestValue.TEST_NAME_ONE);
        ValidatableResponse responseCreate = userClient.createUser(userStellar).assertThat().statusCode(HTTP_OK);
        String accessTokenWithBearer = responseCreate.extract().path("accessToken");
        String accessToken = accessTokenWithBearer.replace("Bearer ", "");

        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add(TestValue.TEST_BAD_BUN);
        ingredients.add(TestValue.TEST_FILLING_TWO);
        OrderStellar orderStellar = new OrderStellar(ingredients);
        ValidatableResponse response = orderClient.orderWithAuth(accessToken, orderStellar)
                .assertThat().statusCode(HTTP_INTERNAL_ERROR);
    }

    @After
    @Step("Очистка данных после выполнения тестов")
    public void clearData() {
        try {
            UserStellar userStellar = new UserStellar(TestValue.TEST_LOGIN_ONE, TestValue.TEST_PASSWORD_ONE, TestValue.TEST_NAME_ONE);
            ValidatableResponse responseLogin = userClient.loginUser(userStellar);
            String accessTokenWithBearer = responseLogin.extract().path("accessToken");
            String accessToken = accessTokenWithBearer.replace("Bearer ", "");
            userClient.deleteUser(accessToken);
        } catch (Exception e) {
            System.out.println("Тест завершился без удаления пользователя.");
        }
    }
}
