package org.example.user;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.example.user.base.BurgerRestClient;
import org.example.user.model.User;
import org.example.user.model.UserCredential;

import static io.restassured.RestAssured.given;


public class UserMethods extends BurgerRestClient {

    private static final String USER_URI = BASE_URI + "auth/";

    @Step("Создать уникального пользователя: {user}")
    public ValidatableResponse create(User user) {
        return
                given()
                        .spec(getbaseReqSpec())
                        .body(user)
                        .when()
                        .post(USER_URI + "register")
                        .then();
    }

    @Step("Логин под существующим пользователем: {userCredential}")
    public ValidatableResponse login(UserCredential userCredential) {
        return
                given()
                        .spec(getbaseReqSpec())
                        .body(userCredential)
                        .when()
                        .post(USER_URI + "login")
                        .then();
    }

    @Step("Изменение данных пользователя: {token}")
    public ValidatableResponse сhange(User user, String token) {
        return
                given()
                        .spec(getbaseReqSpec())
                        .auth().oauth2(token)
                        .body(user)
                        .when()
                        .patch(USER_URI + "user")
                        .then();
    }

    @Step("Удаление пользователя: {token}")
    public ValidatableResponse delete(String token) {
        return
                given()
                        .spec(getbaseReqSpec())
                        .auth().oauth2(token)
                        .when()
                        .delete(USER_URI + "user")
                        .then();
    }
}
