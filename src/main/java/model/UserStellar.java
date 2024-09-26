package model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Класс, представляющий пользователя Stellar Burgers.
 * <p>
 * Пользователь включает такие поля, как email, пароль и имя.
 */
public class UserStellar {

    /**
     * Email пользователя.
     */
    @NotNull
    private String email;

    /**
     * Пароль пользователя.
     */
    @NotNull
    private String password;

    /**
     * Имя пользователя.
     */
    @Nullable
    private String name;

    /**
     * Конструктор для создания нового пользователя Stellar Burgers.
     *
     * @param email    Email пользователя. Не может быть null.
     * @param password Пароль пользователя. Не может быть null.
     * @param name     Имя пользователя. Может быть null.
     */
    public UserStellar(@NotNull String email, @NotNull String password, @Nullable String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    /**
     * Возвращает email пользователя.
     *
     * @return email пользователя. Никогда не возвращает null.
     */
    @NotNull
    public String getEmail() {
        return email;
    }

    /**
     * Возвращает пароль пользователя.
     *
     * @return пароль пользователя. Никогда не возвращает null.
     */
    @NotNull
    public String getPassword() {
        return password;
    }

    /**
     * Возвращает имя пользователя.
     *
     * @return имя пользователя. Может быть null.
     */
    @Nullable
    public String getName() {
        return name;
    }

    /**
     * Устанавливает email пользователя.
     *
     * @param email новый email. Не может быть null.
     */
    public void setEmail(@NotNull String email) {
        this.email = email;
    }

    /**
     * Устанавливает пароль пользователя.
     *
     * @param password новый пароль. Не может быть null.
     */
    public void setPassword(@NotNull String password) {
        this.password = password;
    }

    /**
     * Устанавливает имя пользователя.
     *
     * @param name новое имя пользователя. Может быть null.
     */
    public void setName(@Nullable String name) {
        this.name = name;
    }
}
