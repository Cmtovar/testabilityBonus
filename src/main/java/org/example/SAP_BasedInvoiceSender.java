package org.example;

import java.util.ArrayList;
import java.util.List;

// Class responsible for sending low-valued invoices to the SAP system
public class SAP_BasedInvoiceSender {

    private final FilterInvoice filter;  // Dependency for filtering invoices
    private final SAP sap;  // Dependency for sending invoices to the SAP system

    // Constructor that uses dependency injection to initialize the filter and sap objects
    public SAP_BasedInvoiceSender(FilterInvoice filter, SAP sap) {
        this.filter = filter;
        this.sap = sap;
    }

    // Method to send all low-valued invoices to the SAP system
    public List<Invoice> sendLowValuedInvoices() {
        List<Invoice> lowValuedInvoices = filter.lowValueInvoices();
        List<Invoice> failedInvoices = new ArrayList<>();


        for (Invoice invoice : lowValuedInvoices) {  // Iterates through each invoice in the list
            // Try to send each invoice
            try {
                sap.send(invoice);  // Sends the current invoice to the SAP system
            } catch (FailToSendSAPInvoiceException e) {
                // If send fails, collect the failed invoice
                failedInvoices.add(invoice);
                // continue onto the remaining invoices
            }
        }

        // Updated for requirement 3: returns list of invoices that failed to send
        return failedInvoices;
    }
}
