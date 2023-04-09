import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import org.example.order.OrdersMethods;
import org.example.order.model.Order;
import org.example.user.UserMethods;
import org.example.user.model.User;
import org.example.user.model.UserGenerator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class OrderTest {
    private UserMethods userMethods;
    private OrdersMethods orderMethods;
    private String token;
    private String tokenForAuth;
    String[] ingredients;

    @Before
    public void globalSetUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Before
    public void setUp() {
        userMethods = new UserMethods();
        orderMethods = new OrdersMethods();
    }

    //    Создание заказа:
    //    с авторизацией,
    //    с ингредиентами
    @Test
    @DisplayName("Order - Создание заказа с авторизацией и с ингредиентами")
    @Description("Basic test for /api/orders endpoint")
    public void createOrderWithAuthAndIngredients() {
        User user = UserGenerator.getRandom();
        ValidatableResponse createResponse = userMethods.create(user);
        int statusCode = createResponse.extract().statusCode();
        boolean isUserCreated = createResponse.extract().path("success");
        assertEquals("Status code is not correct", 200, statusCode);
        assertTrue("User is not created", isUserCreated);
        token = createResponse.extract().path("accessToken");
        tokenForAuth = token.substring(7);
        assertNotNull("User token not created", token);

        ingredients = new String[]{"61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f"};
        Order order = new Order(ingredients);
        ValidatableResponse createOrderResponse = orderMethods.create(order, tokenForAuth);
        int createOrderStatusCode = createOrderResponse.extract().statusCode();
        boolean isOrderCreated = createOrderResponse.extract().path("success");
        assertEquals("Status code is not correct", 200, createOrderStatusCode);
        assertTrue("Order is not created", isOrderCreated);

        userMethods.delete(tokenForAuth);
    }

    //    Создание заказа:
    //    с авторизацией,
    //    без ингредиентов
    @Test
    @DisplayName("Order - Создание заказа с авторизацией и без ингредиентов")
    @Description("Basic test for /api/orders endpoint")
    public void createOrderWithAuthAndWithoutIngredients() {
        User user = UserGenerator.getRandom();
        ValidatableResponse createResponse = userMethods.create(user);
        int statusCode = createResponse.extract().statusCode();
        boolean isUserCreated = createResponse.extract().path("success");
        assertEquals("Status code is not correct", 200, statusCode);
        assertTrue("User is not created", isUserCreated);
        token = createResponse.extract().path("accessToken");
        tokenForAuth = token.substring(7);
        assertNotNull("User token not created", token);

        ingredients = new String[]{};
        Order order = new Order(ingredients);
        ValidatableResponse createOrderResponse = orderMethods.create(order, tokenForAuth);
        int createOrderStatusCode = createOrderResponse.extract().statusCode();
        String isOrderCreated = createOrderResponse.extract().path("message");
        String expectedMessage = "Ingredient ids must be provided";

        assertEquals("Status code is not correct", 400, createOrderStatusCode);
        assertEquals("Message is not correct", expectedMessage, isOrderCreated);

        userMethods.delete(tokenForAuth);
    }

    //Создание заказа:
    //с авторизацией,
    //с неверным хешем ингредиентов.
    @Test
    @DisplayName("Order - Создание заказа с авторизацией и с неверным хешем ингредиентов")
    @Description("Basic test for /api/orders endpoint")
    public void createOrderWithAuthAndWithIncorrectIngredients() {
        User user = UserGenerator.getRandom();
        ValidatableResponse createResponse = userMethods.create(user);
        int statusCode = createResponse.extract().statusCode();
        boolean isUserCreated = createResponse.extract().path("success");
        assertEquals("Status code is not correct", 200, statusCode);
        assertTrue("User is not created", isUserCreated);
        token = createResponse.extract().path("accessToken");
        tokenForAuth = token.substring(7);
        assertNotNull("User token not created", token);

        ingredients = new String[]{"60d3b41abdacab0026a733c6", "609646e4dc916e00276b287"};
        Order order = new Order(ingredients);
        ValidatableResponse createOrderResponse = orderMethods.create(order, tokenForAuth);
        int createOrderStatusCode = createOrderResponse.extract().statusCode();

        assertEquals("Status code is not correct", 500, createOrderStatusCode);

        userMethods.delete(tokenForAuth);
    }

    //    Создание заказа:
    //    без авторизации
    @Test
    @DisplayName("Order - Создание заказа без авторизации")
    @Description("Basic test for /api/orders endpoint")
    public void createOrderWithoutAuth() {
        ingredients = new String[]{"61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f"};
        Order order = new Order(ingredients);
        ValidatableResponse createOrderResponse = orderMethods.create(order, " ");
        int createOrderStatusCode = createOrderResponse.extract().statusCode();
        boolean isOrderCreated = createOrderResponse.extract().path("success");
        assertEquals("Status code is not correct", 200, createOrderStatusCode);
        assertTrue("Order is not created", isOrderCreated);
    }

    //    Получение заказов конкретного пользователя:
//    авторизованный пользователь,
    @Test
    @DisplayName("Order - Получение заказов конкретного пользователя: авторизованный пользователь")
    @Description("Basic test for /api/orders endpoint")
    public void getUserOrdersWithAuth() {
        User user = UserGenerator.getRandom();
        ValidatableResponse createResponse = userMethods.create(user);
        int statusCode = createResponse.extract().statusCode();
        boolean isUserCreated = createResponse.extract().path("success");
        assertEquals("Status code is not correct", 200, statusCode);
        assertTrue("User is not created", isUserCreated);
        token = createResponse.extract().path("accessToken");
        tokenForAuth = token.substring(7);
        assertNotNull("User token not created", token);

        ValidatableResponse getOrdersResponse = orderMethods.getOrderList(tokenForAuth);
        int getOrdersStatusCode = getOrdersResponse.extract().statusCode();
        boolean isOrdersGet = createResponse.extract().path("success");
        assertEquals("Status code is not correct", 200, getOrdersStatusCode);
        assertTrue("Orders is not get", isOrdersGet);

        userMethods.delete(tokenForAuth);
    }

    //    Получение заказов конкретного пользователя:
    //    неавторизованный пользователь.
    @Test
    @DisplayName("Order - Получение заказов конкретного пользователя: неавторизованный пользователь")
    @Description("Basic test for /api/orders endpoint")
    public void getUserOrdersWithoutAuth() {
        ValidatableResponse getOrdersResponse = orderMethods.getOrderList(" ");
        int getOrdersStatusCode = getOrdersResponse.extract().statusCode();
        String isOrdersGet = getOrdersResponse.extract().path("message");
        String expectedMessage = "You should be authorised";
        assertEquals("Status code is not correct", 401, getOrdersStatusCode);
        assertEquals("Message is not correct", expectedMessage, isOrdersGet);
    }
}
