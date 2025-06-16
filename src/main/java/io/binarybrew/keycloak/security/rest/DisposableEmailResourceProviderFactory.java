/**
 * Factory for creating instances of {@link DisposableEmailResourceProvider}.
 * <p>
 * This class implements the Keycloak RealmResourceProviderFactory interface to provide
 * a factory for creating DisposableEmailResourceProvider instances. It registers
 * the REST endpoints for disposable email domain operations with Keycloak's REST API.
 * <p>
 * The factory is responsible for:
 * 1. Providing a unique ID that will be part of the REST endpoint URL
 * 2. Creating new instances of the resource provider when requested
 * 3. Handling initialization and cleanup of resources
 * <p>
 * The resource provider created by this factory implements service account authentication
 * with bearer token verification to ensure only authorized service accounts with the
 * realm-admin role can access the endpoints.
 *
 * @author chintan
 */
package io.binarybrew.keycloak.security.rest;

import org.keycloak.Config.Scope;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

public class DisposableEmailResourceProviderFactory implements RealmResourceProviderFactory {

    /**
     * The unique identifier for this resource provider factory.
     * <p>
     * This ID is used by Keycloak to identify this provider and forms part of the
     * REST endpoint URL path. The full endpoint URL will be:
     * /auth/realms/{realm-name}/{ID}/refresh-domain-list
     */
    public static final String ID = "brew-disposable-email-resource-provider";

    /**
     * Returns the unique identifier for this resource provider factory.
     * <p>
     * This method is called by Keycloak to get the ID that will be used in the REST endpoint URL.
     *
     * @return The provider ID string
     */
    @Override
    public String getId() {
        return ID;
    }

    /**
     * Creates a new instance of the DisposableEmailResourceProvider.
     * <p>
     * This method is called by Keycloak when a request is made to the REST endpoint
     * associated with this provider. It creates a new instance of the resource provider
     * for each request.
     *
     * @param session The Keycloak session for the current request
     * @return A new DisposableEmailResourceProvider instance
     */
    @Override
    public RealmResourceProvider create(KeycloakSession session) {
        return new DisposableEmailResourceProvider(session);
    }

    @Override
    public void init(Scope config) {}

    @Override
    public void postInit(KeycloakSessionFactory factory) {}

    @Override
    public void close() {}
}
