package core.service;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import integration.api.model.Error;
import integration.api.model.InsertResult;
import integration.api.model.user.auth.AccountDao;
import integration.api.model.user.auth.AuthenticationResponse;
import integration.api.model.user.reg.NewUserRegistrationRequest;
import integration.service.auth.AuthenticationService;
import integration.service.auth.RegistrationService;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import core.model.User;

@RunWith(MockitoJUnitRunner.class)
public class TestUserService {

    @InjectMocks
    private UserService userService;

    @Mock
    private AuthenticationService authService;

    @Mock
    private RegistrationService registrationService;

    @Mock
    private AuthenticationResponse authenticationResponse;

    @Mock
    private AccountDao accountDao;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private static final ObjectId ACCOUNT_ID = ObjectId.get();

    private static final String USER_EMAIL = "john@gmail.com";
    private static final String USER_PASSWORD = "pa55w0rd";
    private static final String USER_ALIAS = "John";

    @Before
    public void init() {

        // Mock a user account
        when(accountDao.getId()).thenReturn(ACCOUNT_ID.toString());
        when(accountDao.getUsername()).thenReturn(USER_EMAIL);
        when(accountDao.getEmail()).thenReturn(USER_EMAIL);
        when(accountDao.getAlias()).thenReturn(USER_ALIAS);
        when(accountDao.getPassword()).thenReturn(USER_PASSWORD);

        // Always return a result when
        when(authService.authenticate(any(String.class), any(String.class))).thenReturn(authenticationResponse);

    }

    @Test
    public void test_user_authentication_succeeds() {
        // Given
        when(authenticationResponse.getAccount()).thenReturn(accountDao);

        // when
        User user = userService.authenticate(USER_EMAIL, USER_PASSWORD);

        // then
        assertThat(user, is(notNullValue()));
        assertThat(user.getId(), is(equalTo(ACCOUNT_ID.toString())));
        assertThat(user.getEmail(), is(equalTo(USER_EMAIL)));
        assertThat(user.getAlias(), is(equalTo(USER_ALIAS)));
    }

    @Test
    public void test_user_authentication_fails() {
        // Given;
        when(authenticationResponse.getAccount()).thenReturn(null);

        // when
        String wrongPassword = USER_PASSWORD + "_is_wrong";
        User user = userService.authenticate(USER_EMAIL, wrongPassword);

        // then
        assertThat(user, is(nullValue()));
    }

    @Test
    public void test_user_is_created() {
        // Given
        InsertResult<AccountDao> result = new InsertResult<AccountDao>(accountDao);
        when(registrationService.register(any(NewUserRegistrationRequest.class))).thenReturn(result);

        // when
        User user = userService.create(USER_EMAIL, USER_ALIAS, USER_PASSWORD);

        // then
        assertThat(user, is(notNullValue()));
        assertThat(user.getId(), is(equalTo(ACCOUNT_ID.toString())));
        assertThat(user.getEmail(), is(equalTo(USER_EMAIL)));
        assertThat(user.getAlias(), is(equalTo(USER_ALIAS)));
    }

    @Test
    public void test_user_creation_fails() {
        // Given
        InsertResult<AccountDao> result = new InsertResult<AccountDao>(new Error("Account creation failed"));
        when(registrationService.register(any(NewUserRegistrationRequest.class))).thenReturn(result);

        exception.expect(RuntimeException.class);

        userService.create(USER_EMAIL, USER_ALIAS, USER_PASSWORD);
    }
}
