package testValue;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * Класс содержит значения для тестирования, такие как логины, пароли, имена пользователей и ингредиенты для заказов.
 * Генерация случайных данных осуществляется с помощью {@link RandomStringUtils}.
 */
public class TestValue extends RandomStringUtils {

    /**
     * Случайный логин для первого пользователя.
     */
    public static final String TEST_LOGIN_ONE = randomAlphabetic(10).toLowerCase() + "@yandex.ru";

    /**
     * Случайный логин для второго пользователя.
     */
    public static final String TEST_LOGIN_TWO = randomAlphabetic(10).toLowerCase() + "@yandex.ru";

    /**
     * Пароль для первого пользователя.
     */
    public static final String TEST_PASSWORD_ONE = "123";

    /**
     * Пароль для второго пользователя.
     */
    public static final String TEST_PASSWORD_TWO = "123qwe";

    /**
     * Случайное имя для первого пользователя.
     */
    public static final String TEST_NAME_ONE = randomAlphabetic(10);

    /**
     * Случайное имя для второго пользователя.
     */
    public static final String TEST_NAME_TWO = randomAlphabetic(10);

    /**
     * Валидный идентификатор булочки для заказа.
     */
    public static final String TEST_BUN = "61c0c5a71d1f82001bdaaa6d";

    /**
     * Неверный идентификатор булочки для заказа.
     */
    public static final String TEST_BAD_BUN = "123";

    /**
     * Валидный идентификатор первого ингредиента для заказа.
     */
    public static final String TEST_FILLING_ONE = "61c0c5a71d1f82001bdaaa7a";

    /**
     * Валидный идентификатор второго ингредиента для заказа.
     */
    public static final String TEST_FILLING_TWO = "61c0c5a71d1f82001bdaaa76";
}
