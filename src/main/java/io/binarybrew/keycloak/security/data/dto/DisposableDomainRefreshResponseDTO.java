/**
 * Data Transfer Object (DTO) for responses from the disposable domain refresh endpoint.
 * <p>
 * This class encapsulates the response data returned by the REST API endpoint
 * that refreshes the list of disposable email domains. It includes information
 * about whether the refresh operation was successful and a message describing
 * the outcome.
 * <p>
 * The class uses Lombok annotations to automatically generate boilerplate code
 * such as getters, setters, constructors, and toString methods.
 *
 * @author chintan
 */
package io.binarybrew.keycloak.security.data.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DisposableDomainRefreshResponseDTO {

    public DisposableDomainRefreshResponseDTO() {}

    public DisposableDomainRefreshResponseDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    /**
     * Indicates whether the domain refresh operation was successful.
     * <p>
     * This field is serialized as "success" in the JSON response.
     */
    @JsonProperty("success")
    private boolean success;

    /**
     * A message describing the outcome of the domain refresh operation.
     * <p>
     * This field provides additional details about the result, especially
     * useful when the operation fails. It is serialized as "message" in
     * the JSON response.
     */
    @JsonProperty("message")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
