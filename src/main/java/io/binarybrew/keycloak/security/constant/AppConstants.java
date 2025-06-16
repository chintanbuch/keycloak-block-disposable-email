/**
 * Constants used throughout the Keycloak Disposable Email Blocker application.
 * <p>
 * This class provides a centralized location for all application constants,
 * particularly those related to REST endpoints, role names, and client identifiers.
 * Using these constants helps maintain consistency and makes it easier to update
 * values if needed.
 * <p>
 * These constants are primarily used in the authentication flow validation and
 * the REST endpoints for managing disposable email domains.
 *
 * @author chintan
 */
package io.binarybrew.keycloak.security.constant;

public class AppConstants {

    /**
     * The path segment for the REST endpoint that refreshes the disposable domain list.
     * <p>
     * This is used in the {@link io.binarybrew.keycloak.security.rest.DisposableEmailResourceProvider}
     * to define the endpoint URL. The full endpoint path will be constructed as 
     * "/realms/{realm}/disposable-email/refresh-domain-list" and accepts POST requests.
     * <p>
     * When called with proper authentication, this endpoint triggers a refresh of the 
     * disposable email domain list used for validation.
     * 
     * @see io.binarybrew.keycloak.security.rest.DisposableEmailResourceProvider
     * @see io.binarybrew.keycloak.security.rest.DisposableEmailManager
     */
    public static final String POST_PATH = "refresh-domain-list";

    /**
     * The name of the realm management client role in Keycloak.
     * <p>
     * This constant is used when checking if a user has the necessary permissions
     * to perform administrative actions on the realm, particularly when refreshing
     * the disposable email domain list.
     * <p>
     * In Keycloak, the "realm-management" client contains roles that define various
     * administrative permissions.
     * 
     * @see io.binarybrew.keycloak.security.rest.DisposableEmailResourceProvider
     */
    public static final String ROLE_REALM_MANAGEMENT = "realm-management";

    /**
     * The name of the realm administrator role in Keycloak.
     * <p>
     * This constant represents the role that grants full administrative access to a realm.
     * Users with this role can perform all administrative actions, including refreshing
     * the disposable email domain list.
     * <p>
     * This role is typically assigned to users who need to manage all aspects of a Keycloak realm.
     * 
     * @see io.binarybrew.keycloak.security.rest.DisposableEmailResourceProvider
     */
    public static final String ROLE_REALM_ADMIN = "realm-admin";

}
