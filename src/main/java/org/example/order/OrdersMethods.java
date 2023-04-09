package org.example.order;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.example.order.model.Order;
import org.example.user.base.BurgerRestClient;

import static io.restassured.RestAssured.given;

public class OrdersMethods extends BurgerRestClient {
    private static final String ORDER_URI = BASE_URI + "orders";

    @Step("Создание заказа: {order}")
    public ValidatableResponse create(Order order, String token) {
        return
                given()
                        .spec(getbaseReqSpec())
                        .auth().oauth2(token)
                        .body(order)
                        .when()
                        .post(ORDER_URI)
                        .then();
    }

    @Step("Получение списка заказов конкретного пользователя")
    public ValidatableResponse getOrderList(String token) {
        return
                given()
                        .spec(getbaseReqSpec())
                        .auth().oauth2(token)
                        .when()
                        .get(ORDER_URI)
                        .then();
    }
}
