/**
 * A form action implementation that validates email domains during user registration and profile updates.
 * <p>
 * This class implements the Keycloak FormAction interface to provide validation of email domains
 * during the registration and profile update processes. It specifically checks if the provided
 * email address belongs to a known disposable email domain and prevents registration or updates
 * if such domains are detected.
 * <p>
 * The validation is performed by the {@link io.binarybrew.keycloak.security.rest.DisposableEmailManager}
 * which maintains a list of known disposable email domains.
 *
 * @author chintan
 */
package io.binarybrew.keycloak.security.actions;

import io.binarybrew.keycloak.security.rest.DisposableEmailManager;
import jakarta.ws.rs.core.MultivaluedMap;
import org.jboss.logging.Logger;
import org.keycloak.authentication.FormAction;
import org.keycloak.authentication.FormContext;
import org.keycloak.authentication.ValidationContext;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;

import java.util.ArrayList;
import java.util.List;

public class EmailDomainValidationFormAction implements FormAction {

    private static final Logger LOGGER = Logger.getLogger(EmailDomainValidationFormAction.class);

    @Override
    public void buildPage(FormContext formContext, LoginFormsProvider loginFormsProvider) {
    }

    /**
     * Validates the email domain during registration or profile update.
     * <p>
     * This method performs two validations:
     * 1. Checks if the email field is not empty
     * 2. Checks if the email domain is not a known disposable email domain
     * <p>
     * If any validation fails, appropriate error messages are added and the validation
     * is marked as failed. Otherwise, the validation is marked as successful.
     *
     * @param validationContext The validation context containing form data and methods to report validation results
     */
    @Override
    public void validate(ValidationContext validationContext) {
        MultivaluedMap<String, String> formData = validationContext.getHttpRequest().getDecodedFormParameters();
        String email = formData.getFirst("email");

        boolean isError = false;
        List<FormMessage> errors = new ArrayList<>();

        if (email == null || email.isEmpty()) {
            LOGGER.error("Email is null or empty during email domain validation");
            // validationContext.error(Errors.INVALID_REGISTRATION);
            errors.add(new FormMessage("email", "Email is required."));
            isError = true;
        }

        DisposableEmailManager disposableManager = DisposableEmailManager.getInstance();
        boolean isDisposable = disposableManager.isDisposableEmail(email);
        LOGGER.debugf("Disposable email check result for %s: %s", email, isDisposable);
        if (isDisposable) {
            LOGGER.debugf("Disposable email detected: %s", email);
            // validationContext.error(Errors.INVALID_REGISTRATION);
            errors.add(new FormMessage("email", "Invalid email domain."));
            isError = true;
        }

        if (isError) {
            validationContext.validationError(formData, errors);
        } else {
            validationContext.success();
        }
    }

    @Override
    public void success(FormContext formContext) {
    }

    /**
     * Determines if this action requires a user to be present.
     * <p>
     * This method returns false because email domain validation can be performed
     * during registration when no user exists yet.
     *
     * @return false, indicating that a user is not required for this action
     */
    @Override
    public boolean requiresUser() {
        return false;
    }

    /**
     * Checks if this action is configured for the given user.
     * <p>
     * This method always returns true because email domain validation
     * is always applicable for all users.
     *
     * @param keycloakSession The Keycloak session
     * @param realmModel The realm model
     * @param userModel The user model
     * @return true, indicating that this action is configured for all users
     */
    @Override
    public boolean configuredFor(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel) {
    }

    @Override
    public void close() {
    }

}
