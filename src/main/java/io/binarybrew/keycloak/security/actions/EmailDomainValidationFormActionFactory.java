/**
 * Factory for creating instances of {@link EmailDomainValidationFormAction}.
 * <p>
 * This class implements the Keycloak FormActionFactory interface to provide
 * a factory for creating EmailDomainValidationFormAction instances. It also
 * defines metadata about the form action, such as its display name, configuration
 * options, and requirement choices.
 * <p>
 * During initialization, this factory also triggers the loading of disposable
 * email domains to ensure they are available for validation.
 *
 * @author chintan
 */
package io.binarybrew.keycloak.security.actions;

import io.binarybrew.keycloak.security.rest.DisposableEmailManager;
import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.authentication.FormAction;
import org.keycloak.authentication.FormActionFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.List;

public class EmailDomainValidationFormActionFactory implements FormActionFactory {

    /**
     * The unique identifier for this form action provider.
     * This ID is used by Keycloak to identify and reference this specific form action.
     */
    private static final String PROVIDER_ID = "brew-email-domain-validation";

    private static final Logger LOGGER = Logger.getLogger(EmailDomainValidationFormActionFactory.class);

    /**
     * Returns the unique identifier for this form action provider.
     *
     * @return The provider ID string
     */
    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    /**
     * Creates a new instance of the EmailDomainValidationFormAction.
     *
     * @param session The Keycloak session
     * @return A new EmailDomainValidationFormAction instance
     */
    @Override
    public FormAction create(KeycloakSession session) {
        return new EmailDomainValidationFormAction();
    }

    /**
     * Initializes this factory.
     * <p>
     * This method is called when the factory is first created. It refreshes the list
     * of disposable email domains to ensure they are available for validation.
     *
     * @param scope The configuration scope
     */
    @Override
    public void init(Config.Scope scope) {
        DisposableEmailManager.getInstance().refreshDisposableDomains();
        LOGGER.info("DisposableEmailManager initialized during EmailDomainValidationFormActionFactory init method.");
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {}

    /**
     * Returns the display name for this form action.
     * <p>
     * This name is shown in the Keycloak admin console.
     *
     * @return The display name
     */
    @Override
    public String getDisplayType() {
        return "Email Domain Validation";
    }

    /**
     * Returns the reference category for this form action.
     * <p>
     * This method returns null because this form action doesn't
     * belong to any specific category.
     *
     * @return null, indicating no specific category
     */
    @Override
    public String getReferenceCategory() {
        return null;
    }

    /**
     * Determines if this form action is configurable.
     * <p>
     * This method returns false because this form action doesn't
     * have any configurable properties.
     *
     * @return false, indicating that this form action is not configurable
     */
    @Override
    public boolean isConfigurable() {
        return false;
    }

    /**
     * Returns the available requirement choices for this form action.
     * <p>
     * This method returns an array containing REQUIRED and DISABLED options,
     * indicating that this form action can either be required or disabled.
     *
     * @return An array of requirement choices
     */
    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return new AuthenticationExecutionModel.Requirement[]{
                AuthenticationExecutionModel.Requirement.REQUIRED,
                AuthenticationExecutionModel.Requirement.DISABLED
        };
    }

    /**
     * Determines if this form action allows user setup.
     * <p>
     * This method returns false because this form action doesn't
     * require any user setup.
     *
     * @return false, indicating that user setup is not allowed
     */
    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    /**
     * Returns the help text for this form action.
     * <p>
     * This method returns an empty string because no specific help text
     * is provided for this form action.
     *
     * @return An empty string
     */
    @Override
    public String getHelpText() {
        return "Validates email domains during registration to prevent the use of disposable or blacklisted domains.";
    }

    /**
     * Returns the configuration properties for this form action.
     * <p>
     * This method returns an empty list because this form action
     * doesn't have any configurable properties.
     *
     * @return An empty list of provider config properties
     */
    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return List.of();
    }

    @Override
    public void close() {}
}
