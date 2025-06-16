## Overview

This extension adds functionality to Keycloak to prevent users from registering with disposable email addresses (like those from 10MinuteMail, GuerrillaMail, etc.). It provides:

Key features:
- Blocks registration with disposable email domains
- Uses the [zliio/disposable](https://github.com/ZliIO/zliio-disposable) library to validate email domains
- Provides a REST API to refresh the list of disposable domains
- Seamlessly integrates with Keycloak's validation system

## Requirements

- Java 17 or higher
- Tested with Keycloak 26.2.5

## Installation

1. Build the project:
   ```bash
   make build
   ```

2. Copy the generated JAR file to Keycloak's providers directory:
   ```bash
   cp target/email-domain-validation.jar /path/to/keycloak/providers/
   ```

3. Restart Keycloak to load the extension.

[//]: # (## Usage)
## Configuration

[//]: # (### Configuring the Validator)
### 1. Update the Registration Flow

To use the disposable email validator in your Keycloak realm:

1. Log in to the Keycloak Admin Console
2. Select your realm
3. Go to Authentication > Flows
4. Duplicate "registration" flow and rename "registration with disposable email check" or whatever you want
5. Edit the new flow and click "Add execution"
6. Add "Email Domain Validation" from the list and set it to "Required"
7. Finally, Bind a newly created flow to "Registration flow"

### 2. Refreshing the Disposable Email Domain List

The list of disposable email domains is automatically loaded when the extension starts. To manually refresh the list:

Send a POST request to the following endpoint:
```
https://<YOUR-KEYCLOAK-SERVER>/realms/<YOUR-REALM-NAME>/brew-disposable-email-resource-provider/refresh-domain-list
```

This endpoint requires authentication with a bearer token from a service account. The service account must have the "realm-admin" role in the "realm-management" client. Regular user accounts, even with admin roles, cannot access this endpoint.

Example using curl with a service account token:
```bash
curl -X POST \
  https://<YOUR-KEYCLOAK-SERVER>/realms/<YOUR-REALM-NAME>/brew-disposable-email-resource-provider/refresh-domain-list \
  -H 'Authorization: Bearer <service-account-access-token>' \
  -H 'Content-Type: application/json'
```

To get a service account token, you need to:
1. Create a client in Keycloak with "Service Accounts Enabled" set to ON
2. Assign the "realm-admin" role from the "realm-management" client to this service account
3. Request a token using the client credentials grant type:
```bash
curl -X POST \
  https://<YOUR-KEYCLOAK-SERVER>/realms/<REALM-NAME>/protocol/openid-connect/token \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'grant_type=client_credentials&client_id=<YOUR-CLIENT-ID>&client_secret=<YOUR-CLIENT-SECRET>'
```

### Authentication Flow

The refresh endpoint uses the following authentication flow:
1. Verifies that the client is a service account
2. Extracts and validates the bearer token 
3. Checks if the service account has the "realm-admin" role in the "realm-management" client
4. Only proceeds with the refresh operation if all checks pass
