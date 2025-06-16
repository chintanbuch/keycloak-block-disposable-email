/**
 * A singleton manager class that handles the validation and management of disposable email domains.
 * <p>
 * This class provides functionality to check if an email address belongs to a known disposable
 * email domain and to refresh the list of disposable domains from external sources. It uses
 * the com.zliio.disposable library to perform the actual validation and domain list management.
 * <p>
 * The class follows the singleton pattern to ensure only one instance exists throughout the application,
 * which helps maintain a single source of truth for disposable email validation and reduces
 * unnecessary resource usage.
 * 
 * @author chintan
 */
package io.binarybrew.keycloak.security.rest;

import com.zliio.disposable.Disposable;
import io.binarybrew.keycloak.security.actions.EmailDomainValidationFormActionFactory;
import io.binarybrew.keycloak.security.constant.ResponseMessageConstants;
import org.jboss.logging.Logger;

public class DisposableEmailManager {

    /**
     * The singleton instance of this class.
     * This static field holds the single instance of DisposableEmailManager that will be used
     * throughout the application.
     */
    private static DisposableEmailManager instance;

    /**
     * The disposable email validator from the com.zliio.disposable library.
     * This object provides the core functionality for validating emails and refreshing
     * the list of disposable domains.
     */
    private final Disposable disposable;

    private static final Logger LOGGER = Logger.getLogger(EmailDomainValidationFormActionFactory.class);

    /**
     * Private constructor to enforce the singleton pattern.
     * Initializes the disposable email validator without refreshing domains.
     * The domains will be refreshed explicitly when needed.
     */
    private DisposableEmailManager() {
        this.disposable = new Disposable();
        LOGGER.info("DisposableEmailManager initialized.");
    }

    /**
     * Gets the singleton instance of DisposableEmailManager.
     * This method is synchronized to ensure thread safety when creating the instance.
     * If the instance doesn't exist yet, it will be created; otherwise, the existing
     * instance will be returned.
     *
     * @return The singleton instance of DisposableEmailManager
     */
    public static synchronized DisposableEmailManager getInstance() {
        if (instance == null) {
            instance = new DisposableEmailManager();
        }
        return instance;
    }

    /**
     * Checks if the provided email address belongs to a known disposable email domain.
     * This method delegates to the underlying disposable email validator to perform the check.
     *
     * @param email The email address to check
     * @return true if the email is from a disposable domain, false otherwise
     */
    public boolean isDisposableEmail(String email) {
        return !disposable.validate(email);
    }

    /**
     * Refreshes the list of disposable email domains from external sources.
     * This method updates the internal cache of disposable domains to ensure
     * that the validator has the most up-to-date information. It logs the
     * success or failure of the refresh operation.
     *
     * @return true if the refresh was successful, false if an error occurred
     */
    public boolean refreshDisposableDomains() {
        try {
            disposable.refreshDisposableDomains();
            LOGGER.info(ResponseMessageConstants.DISPOSABLE_EMAIL_DOMAIN_REFRESH_SUCCESS);
            return true;
        } catch (Exception e) {
            LOGGER.error(ResponseMessageConstants.DISPOSABLE_EMAIL_DOMAIN_REFRESH_FAILED, e);
            return false;
        }
    }
}
