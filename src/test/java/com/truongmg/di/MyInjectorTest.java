package com.truongmg.di;

import com.truongmg.di.services.*;
import com.truongmg.di.services.DependencyContainer;
import org.junit.*;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class MyInjectorTest {

    static DependencyContainer dependencyContainer;
    UserService userService;
    UserDeviceService deviceService;
    OtherService otherService;

    @BeforeClass
    public static void beforeClass() {
        dependencyContainer = MyInjector.run(MyInjectorTest.class);
    }

    @AfterClass
    public static void afterClass() {
        dependencyContainer = null;
    }

    @Before
    public void setUp() throws Exception {
        System.out.println("running");
        userService = dependencyContainer.getService(UserService.class);
        deviceService = dependencyContainer.getService(UserDeviceService.class);
        otherService = dependencyContainer.getService(OtherService.class);
    }

    @After
    public void tearDown() throws Exception {
        System.out.println("tearDown");
        userService = null;
        deviceService = null;
        otherService = null;
    }

    @Test
    public void testInit() {
        assertThat(userService, notNullValue());
        assertThat(deviceService, notNullValue());

    }

    @Test
    public void testClassImplementation_expectActualImplementationClass() {
        String clsName = userService.getClass().getName();
        assertThat(userService, is(instanceOf(UserServiceImpl.class)));
        assertThat(clsName, is(UserServiceImpl.class.getName()));

        clsName = deviceService.getClass().getName();
        assertThat(deviceService, is(instanceOf(UserDeviceServiceImpl.class)));
        assertThat(clsName, is(UserDeviceServiceImpl.class.getName()));
    }

    @Test
    public void testDeclaredBeanInConfigFile_expectInjectedSuccessfully() {
        assertThat(otherService, notNullValue());
        otherService.printMessage();
    }

    @Test
    public void testGetAllUsers_expectGettingResults() {
        List<String> users = userService.getUsers();
        assertThat(users, notNullValue());
        assertThat(users.size(), greaterThan(0));
        System.out.println(users);
    }

}