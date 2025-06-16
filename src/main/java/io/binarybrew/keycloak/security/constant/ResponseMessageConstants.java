/**
 * Constants for error messages used throughout the application.
 * <p>
 * This class provides a centralized location for all error message constants,
 * including both message keys for internationalization and actual error message texts.
 * Using these constants helps maintain consistency in error reporting and makes it
 * easier to update messages if needed.
 *
 * @author chintan
 */
package io.binarybrew.keycloak.security.constant;

public class ResponseMessageConstants {

    /**
     * Error message for when a user attempts to access a protected resource without authentication.
     * This message is used in the REST API response and in logs to indicate an authentication failure.
     */
    public static final String USER_NOT_AUTHENTICATED = "User is not authenticated.";

    /**
     * Error message for when an authorization token is required but not provided.
     * This message is used in the REST API response and in logs to indicate a missing token.
     */
    public static final String MISSING_AUTHORIZATION_TOKEN = "Authorization token is missing.";

    /**
     * Error message for unauthorized attempts to refresh the disposable email domain list.
     * This message is used in the REST API when a user without proper permissions
     * attempts to refresh the domain list.
     */
    public static final String UNAUTHORIZED_REFRESH_ATTEMPT = "Unauthorized attempt to refresh disposable email domains";

    /**
     * Success message for when the disposable email domain list is refreshed successfully.
     * This message is used in the REST API response and in logs to indicate a successful refresh.
     */
    public static final String DISPOSABLE_EMAIL_DOMAIN_REFRESH_SUCCESS = "Disposable email domains refreshed successfully";

    /**
     * Error message for when the disposable email domain list refresh fails.
     * This message is used in the REST API response and in logs to indicate a failed refresh.
     */
    public static final String DISPOSABLE_EMAIL_DOMAIN_REFRESH_FAILED = "Failed to refresh disposable email domains";

    /**
     * Error message for when an exception occurs during the disposable email domain list refresh.
     * This message is used in logs to indicate an error during the refresh process.
     */
    public static final String DISPOSABLE_EMAIL_DOMAIN_REFRESH_ERROR = "Error refreshing disposable email domains";

}
