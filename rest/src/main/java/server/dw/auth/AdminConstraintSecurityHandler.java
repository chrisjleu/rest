/**
 * 
 */
package server.dw.auth;

import java.io.IOException;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.MappedLoginService;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.UserIdentity;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Password;

/**
 * Ensures only users with the correct role can access the admin pages.
 */
public class AdminConstraintSecurityHandler extends ConstraintSecurityHandler {

    private static final String ADMIN_ROLE = "admin";

    public AdminConstraintSecurityHandler(final String userName, final String password) {
        final Constraint constraint = new Constraint(Constraint.__BASIC_AUTH, ADMIN_ROLE);
        constraint.setAuthenticate(true);
        constraint.setRoles(new String[] { ADMIN_ROLE });
        final ConstraintMapping constraintMapping = new ConstraintMapping();
        constraintMapping.setConstraint(constraint);
        constraintMapping.setPathSpec("/*");
        setAuthenticator(new BasicAuthenticator());
        addConstraintMapping(constraintMapping);
        setLoginService(new AdminMappedLoginService(userName, password, ADMIN_ROLE));
    }

    private class AdminMappedLoginService extends MappedLoginService {

        public AdminMappedLoginService(final String userName, final String password, final String role) {
            putUser(userName, new Password(password), new String[] { role });
        }

        @Override
        public String getName() {
            return "Admin";
        }

        @Override
        protected UserIdentity loadUser(final String username) {
            return null;
        }

        @Override
        protected void loadUsers() throws IOException {
        }
    }
}
