import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import org.example.user.UserMethods;
import org.example.user.model.User;
import org.example.user.model.UserCredential;
import org.example.user.model.UserGenerator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {
    private UserMethods userMethods;
    private String token;
    private String tokenForAuth;

    @Before
    public void globalSetUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Before
    public void setUp() {
        userMethods = new UserMethods();
    }

    //    Создание пользователя:
    //    создать уникального пользователя;
    @Test
    @DisplayName("User - Создание пользователя") // имя теста
    @Description("Basic test for /api/auth/register endpoint") // описание теста
    public void createUserWithValidData() {
        User user = UserGenerator.getRandom();
        ValidatableResponse createResponse = userMethods.create(user);
        int statusCode = createResponse.extract().statusCode();
        boolean isUserCreated = createResponse.extract().path("success");
        assertEquals("Status code is not correct", 200, statusCode);
        assertTrue("User is not created", isUserCreated);

        ValidatableResponse loginResponse = userMethods.login(UserCredential.from(user));
        token = loginResponse.extract().path("accessToken");
        tokenForAuth = token.substring(7);
        assertNotNull("User token not created", token);
        userMethods.delete(tokenForAuth);
    }

    // создать пользователя, который уже зарегистрирован;
    @Test
    @DisplayName("User - Создание пользователя с теми же логином, паролем, именем") // имя теста
    @Description("Basic test for /api/auth/register endpoint") // описание теста
    public void createUserWithSameData() {
        User user = UserGenerator.getRandom();
        ValidatableResponse createResponse = userMethods.create(user);
        int statusCode = createResponse.extract().statusCode();
        boolean isUserCreated = createResponse.extract().path("success");
        assertEquals("Status code is not correct", 200, statusCode);
        assertTrue("User is not created", isUserCreated);

        ValidatableResponse createSecondResponse = userMethods.create(user);
        int secondStatusCode = createSecondResponse.extract().statusCode();
        String isSecondUserCreated = createSecondResponse.extract().path("message");
        String expectedMessage = "User already exists";

        assertEquals("Status code is not correct", 403, secondStatusCode);
        assertEquals("Message is not correct", expectedMessage, isSecondUserCreated);
    }

    // создать пользователя и не заполнить одно из обязательных полей.
    @Test
    @DisplayName("User - Создание пользователя, если одного из полей нет") // имя теста
    @Description("Basic test for /api/auth/register endpoint") // описание теста
    public void createUserWithoutOneField() {
        User user = new User("trtrt@tyyt.ru", "trtrt");
        ValidatableResponse createResponse = userMethods.create(user);
        int statusCode = createResponse.extract().statusCode();
        String isUserCreated = createResponse.extract().path("message");
        String expectedMessage = "Email, password and name are required fields";

        assertEquals("Status code is not correct", 403, statusCode);
        assertEquals("Message is not correct", expectedMessage, isUserCreated);
    }

//    Логин пользователя:
//    логин под существующим пользователем,
    @Test
    @DisplayName("Login - Логин под существующим пользователем") // имя теста
    @Description("Basic test for /api/auth/login endpoint") // описание теста
    public void loginUserWithValidData() {
        User user = UserGenerator.getRandom();
        ValidatableResponse createResponse = userMethods.create(user);
        int statusCode = createResponse.extract().statusCode();
        boolean isUserCreated = createResponse.extract().path("success");
        assertEquals("Status code is not correct", 200, statusCode);
        assertTrue("User is not created", isUserCreated);

        ValidatableResponse loginResponse = userMethods.login(UserCredential.from(user));
        int secondStatusCode = loginResponse.extract().statusCode();
        boolean isUserLogin = loginResponse.extract().path("success");
        assertEquals("Status code is not correct", 200, secondStatusCode);
        assertTrue("User is not login", isUserLogin);

        token = loginResponse.extract().path("accessToken");
        tokenForAuth = token.substring(7);
        assertNotNull("User token not created", token);
        userMethods.delete(tokenForAuth);

    }
    //    Логин пользователя:
    //    логин с неверным логином и паролем.
    @Test
    @DisplayName("Login - Логин с неверным логином и паролем") // имя теста
    @Description("Basic test for /api/auth/login endpoint") // описание теста
    public void loginUserWithInvalidLoginAndPassword() {
        UserCredential userCredential = new UserCredential("login@mail.ru", "qw12");
        ValidatableResponse createResponse = userMethods.login(userCredential);
        int secondStatusCode = createResponse.extract().statusCode();
        String isUserLogin = createResponse.extract().path("message");
        String expectedMessage = "email or password are incorrect";
        assertEquals("Status code is not correct", 401, secondStatusCode);
        assertEquals("Message is not correct", expectedMessage, isUserLogin);

    }

    //Изменение данных пользователя:
//    с авторизацией,
//    Для неавторизованного пользователя — ещё и то, что система вернёт ошибку.

    @Test
    @DisplayName("Change user data - Изменение данных пользователя с авторизацией") // имя теста
    @Description("Basic test for /api/auth/user endpoint") // описание теста
    public void changeUserDataWithAuth() {
        User user = UserGenerator.getRandom();
        ValidatableResponse createResponse = userMethods.create(user);
        int statusCode = createResponse.extract().statusCode();
        boolean isUserCreated = createResponse.extract().path("success");
        assertEquals("Status code is not correct", 200, statusCode);
        assertTrue("User is not created", isUserCreated);

        ValidatableResponse loginResponse = userMethods.login(UserCredential.from(user));
        int secondStatusCode = loginResponse.extract().statusCode();
        boolean isUserLogin = loginResponse.extract().path("success");
        assertEquals("Status code is not correct", 200, secondStatusCode);
        assertTrue("User is not login", isUserLogin);

        token = loginResponse.extract().path("accessToken");
        tokenForAuth = token.substring(7);
        assertNotNull("User token not created", token);
        User userChangeData = new User("trtrt@tyyt.ru", "trtrt", "trttrt");
        ValidatableResponse changeUserData= userMethods.сhange(userChangeData, tokenForAuth);
        int changeUserStatusCode = changeUserData.extract().statusCode();
        boolean isUserChangeData= changeUserData.extract().path("success");
        assertEquals("Status code is not correct", 200, changeUserStatusCode);
        assertTrue("User is not login", isUserChangeData);

        userMethods.delete(tokenForAuth);
    }

    //Изменение данных пользователя:
//    без авторизации,
//    Для неавторизованного пользователя — ещё и то, что система вернёт ошибку.

    @Test
    @DisplayName("Change user data - Изменение данных пользователя без авторизации") // имя теста
    @Description("Basic test for /api/auth/user endpoint") // описание теста
    public void changeUserDataWithoutAuth() {
        User user = UserGenerator.getRandom();
        ValidatableResponse createResponse = userMethods.create(user);
        int statusCode = createResponse.extract().statusCode();
        boolean isUserCreated = createResponse.extract().path("success");
        assertEquals("Status code is not correct", 200, statusCode);
        assertTrue("User is not created", isUserCreated);

        token = createResponse.extract().path("accessToken");
        tokenForAuth = token.substring(7);
        assertNotNull("User token not created", token);

        User userChangeData = new User("trtrt@tyyt.ru", "trtrt", "trttrt");
        ValidatableResponse changeUserData= userMethods.сhange(userChangeData, " ");
        int changeUserStatusCode = changeUserData.extract().statusCode();
        assertEquals("Status code is not correct", 401, changeUserStatusCode);
        String isUserLogin = changeUserData.extract().path("message");
        String expectedMessage = "You should be authorised";
        assertEquals("Message is not correct", expectedMessage, isUserLogin);
        userMethods.delete(tokenForAuth);
    }
}
