/**
 * A REST resource provider for managing disposable email domain operations in Keycloak.
 * <p>
 * This class implements the RealmResourceProvider interface to provide REST endpoints
 * for refreshing the list of disposable email domains. It handles authentication and
 * authorization to ensure only service accounts with appropriate permissions can trigger the refresh.
 * <p>
 * The provider exposes a POST endpoint that allows service accounts with the realm-admin role
 * in the realm-management client to manually refresh the list of disposable email domains
 * used for validation during user registration and profile updates.
 * <p>
 * Authentication is performed using bearer token verification, and the token is checked
 * for the required role permissions before allowing the refresh operation.
 * 
 * @author chintan
 */
package io.binarybrew.keycloak.security.rest;

import io.binarybrew.keycloak.security.actions.EmailDomainValidationFormActionFactory;
import io.binarybrew.keycloak.security.constant.AppConstants;
import io.binarybrew.keycloak.security.constant.ResponseMessageConstants;
import io.binarybrew.keycloak.security.data.dto.DisposableDomainRefreshResponseDTO;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.keycloak.TokenVerifier;
import org.keycloak.http.HttpRequest;
import org.keycloak.models.*;
import org.keycloak.representations.AccessToken;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.services.resource.RealmResourceProvider;

import java.util.Map;

public class DisposableEmailResourceProvider implements RealmResourceProvider {

    /**
     * The Keycloak session associated with this resource provider.
     * This session provides access to various Keycloak services and the current context,
     * including realm and user information needed for authorization checks.
     */
    private final KeycloakSession session;

    /**
     * The authentication result for the current request.
     * This field stores the result of authenticating the bearer token in the request,
     * which is used to determine if the user is authenticated and to retrieve user information.
     */
    private final AuthenticationManager.AuthResult auth;

    private static final Logger LOGGER = Logger.getLogger(EmailDomainValidationFormActionFactory.class);

    /**
     * Constructor for the DisposableEmailResourceProvider.
     * Initializes the provider with the given Keycloak session and authenticates
     * the current request using a bearer token authenticator.
     *
     * @param session The Keycloak session to be used by this provider
     */
    public DisposableEmailResourceProvider(KeycloakSession session) {
        this.session = session;
        this.auth = new AppAuthManager.BearerTokenAuthenticator(session).authenticate();
    }

    /**
     * Returns this instance as the resource object.
     * This method is called by the Keycloak framework to obtain the resource
     * that will handle REST requests.
     *
     * @return This instance as the resource object
     */
    @Override
    public Object getResource() {
        return this;
    }

    /**
     * Closes any resources held by this provider.
     * This method is called by the Keycloak framework when the provider
     * is no longer needed. In this implementation, there are no resources
     * that need to be closed.
     */
    @Override
    public void close() {
        // Nothing to close
    }

    /**
     * REST endpoint for refreshing the list of disposable email domains.
     * This method handles POST requests to refresh the list of disposable email domains
     * used for validation. It performs the following checks:
     * 1. Verifies that the user is authenticated
     * 2. Checks if the client is service account
     * 3. Extracts and verifies the bearer token from Authorization header
     * 4. Checks if the service account has the realm-admin role in realm-management client
     * 5. Calls the DisposableEmailManager to refresh the domains
     * <p>
     * The method returns different responses based on the outcome:
     * - 401 Unauthorized if the user is not authenticated or the token is missing
     * - 400 Bad Request if the HTTP request is not available
     * - 403 Forbidden if the service account doesn't have the required role permissions
     * - 200 OK if the refresh was successful
     * - 500 Internal Server Error if the refresh failed
     *
     * @return A Response object with the appropriate status code and message
     */
    @POST
    @Path(AppConstants.POST_PATH)
    @Produces(MediaType.APPLICATION_JSON)
    public Response refreshDisposableDomains() {
        try {
            // Check if the user is authenticated
            if (auth == null) {
                return Response.status(Response.Status.UNAUTHORIZED).entity(
                        new DisposableDomainRefreshResponseDTO(false, ResponseMessageConstants.USER_NOT_AUTHENTICATED)
                ).build();
            }

            // Check if user has Realm admin role
            ClientModel clientModel = session.getContext().getClient();

            LOGGER.debugf("Service account enabled for client: %s, %s", clientModel.getClientId(), clientModel.isServiceAccountsEnabled());
            if (clientModel.isServiceAccountsEnabled()) {
                // Get the HTTP request from the context
                HttpRequest request = session.getContext().getHttpRequest();
                if (request == null) {
                    return Response.status(Response.Status.BAD_REQUEST).entity(
                            new DisposableDomainRefreshResponseDTO(false, "")
                    ).build();
                    // return Response.status(Response.Status.BAD_REQUEST).build();
                }

                String bearerString = "Bearer ";
                // Extract token from the Authorization header
                String authHeader = request.getHttpHeaders().getHeaderString(HttpHeaders.AUTHORIZATION);
                if (authHeader == null || !authHeader.startsWith(bearerString)) {
                    return Response.status(Response.Status.UNAUTHORIZED).entity(
                            new DisposableDomainRefreshResponseDTO(false, ResponseMessageConstants.MISSING_AUTHORIZATION_TOKEN)
                    ).build();
                }

                String tokenString = authHeader.substring(bearerString.length()); // Remove "Bearer " prefix
                // Parse and verify the token
                TokenVerifier<AccessToken> verifier = TokenVerifier.create(tokenString, AccessToken.class);
                AccessToken token = verifier.getToken();

                // Check for a realm-admin role in realm-management client roles
                Map<String, AccessToken.Access> resourceAccess = token.getResourceAccess();
                if (resourceAccess != null) {
                    AccessToken.Access realmManagementAccess = resourceAccess.get(AppConstants.ROLE_REALM_MANAGEMENT);
                    if (realmManagementAccess != null && realmManagementAccess.getRoles().contains(AppConstants.ROLE_REALM_ADMIN)) {
                        // Client's service account has the realm-admin role
                        // User is authenticated and has admin permissions, proceed with refresh
                        DisposableEmailManager manager = DisposableEmailManager.getInstance();
                        boolean success = manager.refreshDisposableDomains();
                        if (success) {
                            return Response.ok().entity(
                                    new DisposableDomainRefreshResponseDTO(true, ResponseMessageConstants.DISPOSABLE_EMAIL_DOMAIN_REFRESH_SUCCESS)
                            ).build();
                        } else {
                            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                                    new DisposableDomainRefreshResponseDTO(false, ResponseMessageConstants.DISPOSABLE_EMAIL_DOMAIN_REFRESH_FAILED)
                            ).build();
                        }
                    }
                }
            }
            LOGGER.warnf(ResponseMessageConstants.UNAUTHORIZED_REFRESH_ATTEMPT + " by client: %s", clientModel.getClientId());
            return Response.status(Response.Status.FORBIDDEN).entity(new DisposableDomainRefreshResponseDTO(false, ResponseMessageConstants.UNAUTHORIZED_REFRESH_ATTEMPT)).build();
        } catch (Exception e) {
            LOGGER.error(ResponseMessageConstants.DISPOSABLE_EMAIL_DOMAIN_REFRESH_ERROR, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    new DisposableDomainRefreshResponseDTO(false, ResponseMessageConstants.DISPOSABLE_EMAIL_DOMAIN_REFRESH_ERROR)
            ).build();
        }
    }
}
