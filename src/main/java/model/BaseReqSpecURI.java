package model;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.jetbrains.annotations.NotNull;

/**
 * Класс, представляющий базовую настройку для запросов к API Stellar Burgers.
 *
 * <p>Содержит основную ссылку на сервер и метод для получения настроек запросов с заданной конфигурацией.</p>
 */
public class BaseReqSpecURI {

    /**
     * Основной URI для API Stellar Burgers.
     */
    public static final String BASE_URI = "https://stellarburgers.nomoreparties.site";

    /**
     * Возвращает объект {@link RequestSpecification}, который можно использовать в тестах для выполнения запросов.
     *
     * @return базовая спецификация запроса с указанием базового URI и типа содержимого (JSON).
     */
    @NotNull
    public RequestSpecification getBaseReqSpec() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)  // Установка типа контента для запросов (JSON)
                .setBaseUri(BASE_URI)  // Установка базового URI для запросов
                .build();
    }
}
