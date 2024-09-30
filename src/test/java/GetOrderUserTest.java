import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.UserStellar;
import model.OrderStellar;
import order.OrderClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import testValue.TestValue;
import user.UserClient;

import java.util.ArrayList;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;

/**
 * Тесты для получения заказов конкретного пользователя:
 * - авторизованный пользователь,
 * - неавторизованный пользователь.
 */
public class GetOrderUserTest {
    private UserClient userClient;
    private OrderClient orderClient;

    @Before
    @Step("Подготовка окружения: создание клиентов UserClient и OrderClient")
    public void setUp() {
        userClient = new UserClient();
        orderClient = new OrderClient();
    }

    @Test
    @DisplayName("Получение заказов авторизованного пользователя. Ожидаемый ответ: 200")
    @Description("Get запрос на ручку /api/orders для авторизованного пользователя.")
    public void getOrderAuthUser() {
        UserStellar userStellar = createUser(TestValue.TEST_LOGIN_ONE, TestValue.TEST_PASSWORD_ONE, TestValue.TEST_NAME_ONE);
        String accessToken = loginAndGetToken(userStellar);

        ArrayList<String> ingredients = prepareIngredients();
        createOrderWithAuth(accessToken, ingredients);

        ValidatableResponse responseGetOrders = orderClient.getOrderUserAuth(accessToken)
                .assertThat().statusCode(HTTP_OK);
        checkOrderResponse(responseGetOrders, ingredients);
    }

    @Test
    @DisplayName("Получение заказов неавторизованного пользователя. Ожидаемый ответ: 401")
    @Description("Get запрос на ручку /api/orders для неавторизованного пользователя.")
    public void getOrderNotAuthUser() {
        UserStellar userStellar = createUser(TestValue.TEST_LOGIN_ONE, TestValue.TEST_PASSWORD_ONE, TestValue.TEST_NAME_ONE);
        String accessToken = loginAndGetToken(userStellar);

        ArrayList<String> ingredients = prepareIngredients();
        createOrderWithAuth(accessToken, ingredients);

        ValidatableResponse responseGetOrders = orderClient.getOrderUserNotAuth()
                .assertThat().statusCode(HTTP_UNAUTHORIZED);
        checkUnauthorizedOrderResponse(responseGetOrders);
    }

    @After
    @Step("Постусловие: удаление пользователя")
    public void clearData() {
        try {
            UserStellar userStellar = new UserStellar(TestValue.TEST_LOGIN_ONE, TestValue.TEST_PASSWORD_ONE, TestValue.TEST_NAME_ONE);
            String accessToken = loginAndGetToken(userStellar);
            userClient.deleteUser(accessToken);
        } catch (Exception e) {
            System.out.println("Завершилось без удаления");
        }
    }

    @Step("Создание пользователя с email: {0}, password: {1} и именем: {2}")
    private UserStellar createUser(String email, String password, String name) {
        UserStellar userStellar = new UserStellar(email, password, name);
        userClient.createUser(userStellar).assertThat().statusCode(HTTP_OK);
        return userStellar;
    }

    @Step("Авторизация пользователя и извлечение токена")
    private String loginAndGetToken(UserStellar userStellar) {
        ValidatableResponse responseLogin = userClient.loginUser(userStellar).assertThat().statusCode(HTTP_OK);
        String accessTokenWithBearer = responseLogin.extract().path("accessToken");
        return accessTokenWithBearer.replace("Bearer ", "");
    }

    @Step("Подготовка ингредиентов для заказа")
    private ArrayList<String> prepareIngredients() {
        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add(TestValue.TEST_BUN);
        ingredients.add(TestValue.TEST_FILLING_ONE);
        ingredients.add(TestValue.TEST_FILLING_TWO);
        return ingredients;
    }

    @Step("Создание заказа с авторизацией")
    private void createOrderWithAuth(String accessToken, ArrayList<String> ingredients) {
        OrderStellar orderStellar = new OrderStellar(ingredients);
        orderClient.orderWithAuth(accessToken, orderStellar)
                .assertThat().statusCode(HTTP_OK);
    }

    @Step("Проверка ответа на получение заказов авторизованного пользователя")
    private void checkOrderResponse(ValidatableResponse responseGetOrders, ArrayList<String> ingredients) {
        responseGetOrders.assertThat().body("success", equalTo(true))
                .and().body("orders", not(ingredients.isEmpty()));
    }

    @Step("Проверка ответа на получение заказов неавторизованного пользователя")
    private void checkUnauthorizedOrderResponse(ValidatableResponse responseGetOrders) {
        responseGetOrders.assertThat().body("success", equalTo(false))
                .and().body("message", equalTo("You should be authorised"));
    }
}
