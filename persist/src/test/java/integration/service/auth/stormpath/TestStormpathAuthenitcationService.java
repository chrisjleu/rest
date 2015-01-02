package integration.service.auth.stormpath;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import integration.api.model.apikey.ApiToken;

import javax.xml.bind.DatatypeConverter;

import org.apache.oltu.oauth2.common.message.types.TokenType;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.stormpath.sdk.http.HttpMethod;
import com.stormpath.sdk.http.HttpRequest;
import com.stormpath.sdk.oauth.TokenResponse;

@RunWith(MockitoJUnitRunner.class)
public class TestStormpathAuthenitcationService {

    @Mock
    TokenResponse tokenResponse;

    @InjectMocks
    StormpathAuthenticationService stormpathAuthenticationService;

    private final static String API_KEY = "";

    private final static String SECRET = "";

    private static String BASIC_AUTH_HTTP_HEADER_VALUE = "";

    @BeforeClass
    public static void setUp() {
        // Encodes properly the key and secret (username and password) as an HTTP basic authentication header value
        byte[] unencodedkeyAndSecret = API_KEY.concat(":").concat(SECRET).getBytes();
        String base64EncodedKeyAndSecret = DatatypeConverter.printBase64Binary(unencodedkeyAndSecret);
        BASIC_AUTH_HTTP_HEADER_VALUE = "Basic ".concat(base64EncodedKeyAndSecret);
    }

    @Test
    public void test_basic_auth_header_is_constructed_correctly_from_api_key_and_secret() {

        // ...when
        String basicAuthHeader = stormpathAuthenticationService.toAuthorizationHttpHeaderFormat(API_KEY, SECRET);

        // ...then
        assertNotNull(basicAuthHeader);
        assertThat(basicAuthHeader, equalTo(BASIC_AUTH_HTTP_HEADER_VALUE));
    }

    @Test
    public void test_stormpath_http_request_is_constructed_correctly_from_api_key_and_secret() {

        // ...when
        HttpRequest request = stormpathAuthenticationService.buildHttpRequest(API_KEY, SECRET);

        // ...then
        assertThat(request.getMethod(), equalTo(HttpMethod.POST));

        // Check all the HTTP headers are correct
        assertNotNull(request.getHeader("Authorization"));
        assertThat(request.getHeader("Authorization"), equalTo(BASIC_AUTH_HTTP_HEADER_VALUE));

        assertNotNull(request.getHeader("Content-Type"));
        assertThat(request.getHeader("Content-Type"), equalTo("application/x-www-form-urlencoded"));

        // Check request body parameters are present
        assertNotNull(request.getParameters());
        assertThat(request.getParameter("grant_type"), equalTo("client_credentials"));
    }

    @Test
    public void test_api_token_is_built_correctly_from_stormpath_response() {
        // Given
        when(tokenResponse.getAccessToken()).thenReturn("AccessToken");
        when(tokenResponse.getExpiresIn()).thenReturn("3600");
        when(tokenResponse.getRefreshToken()).thenReturn("RefreshToken");
        when(tokenResponse.getScope()).thenReturn("scope");
        when(tokenResponse.getTokenType()).thenReturn(TokenType.BEARER.toString());
        
        // when
        ApiToken token = stormpathAuthenticationService.buildApiToken(tokenResponse);
        
        // then
        assertNotNull(token);
        assertThat(token.getAccessToken(), equalTo("AccessToken"));
        assertThat(token.getExpiresIn(), equalTo("3600"));
        assertThat(token.getRefreshToken(), equalTo("RefreshToken"));
        assertThat(token.getScope(), equalTo("scope"));
        assertThat(token.getTokenType(), equalTo("Bearer"));
    }

}
