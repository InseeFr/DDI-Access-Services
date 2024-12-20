package fr.insee.rmes.exceptions;

import java.io.IOException;

public class RmesExceptionIO extends IOException {

    private final int status;
    private final String details;

    /**
     *
     * @param status
     * @param message
     * @param details
     */
    public RmesExceptionIO(int status, String message, String details) {
        super(message);
        this.status = status;
        this.details = details;
    }

    public RestMessage toRestMessage() {
        return new RestMessage(this.status, this.getMessage(), this.details);
    }
}
