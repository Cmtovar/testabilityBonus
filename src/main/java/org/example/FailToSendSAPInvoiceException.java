package org.example;

public class FailToSendSAPInvoiceException extends RuntimeException {
    // this is a runtime exception, so I don't need to write "throws"
    public FailToSendSAPInvoiceException(String message) {
        super(message);
    }

}
