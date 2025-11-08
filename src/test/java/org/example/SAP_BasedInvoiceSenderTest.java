package org.example;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class SAP_BasedInvoiceSenderTest {
    @Test
    void testWhenLowInvoicesSent() {
        // This test verifies that sendLowValuedInvoices() calls sap.send()
        // must call sap.send() for each invoice returned by the filter

        // create mocks for both dependencies
        FilterInvoice mockFilter = mock(FilterInvoice.class);
        SAP mockSap = mock(SAP.class);

        // create fake invoice data to simulate filter results
        Invoice invoice1 = new Invoice("Customer1", 50);
        Invoice invoice2 = new Invoice("Customer1", 75);

        List<Invoice> fakeInvoices = new ArrayList<>();
        fakeInvoices.add(invoice1);
        fakeInvoices.add(invoice2);

        // stub filter to return fake invoices
        when(mockFilter.lowValueInvoices()).thenReturn(fakeInvoices);

        // create sender with mocked dependencies
        SAP_BasedInvoiceSender sender = new SAP_BasedInvoiceSender(mockFilter, mockSap);

        // Call the method being tested
        sender.sendLowValuedInvoices();

        // Verify sap.send() was called for each invoice
        // this makes sure all low-value invoices are send to SAP_BasedInvoiceSender
        verify(mockSap).send(invoice1);
        verify(mockSap).send(invoice2);
    }

    @Test
    void testWhenNoInvoices() {
        // This test verifies that sendLowValuedInvoices() does NOT
        // call sap.send() when there are no invoices

        // create mocks for both dependencies
        FilterInvoice mockFilter = mock(FilterInvoice.class);
        SAP mockSap = mock(SAP.class);

        // stub filter to return empty list
        List<Invoice> emptyList = new ArrayList<>();
        when(mockFilter.lowValueInvoices()).thenReturn(emptyList);

        // create sender with mocked dependencies
        SAP_BasedInvoiceSender sender = new SAP_BasedInvoiceSender(mockFilter, mockSap);

        // Call the method being tested
        sender.sendLowValuedInvoices();

        // Verify sap.send() was never called
        // since there are no invoices, SAP is expected to not be sent anything
        // using verifyNoInteractions() to check that mockSap wasn't touched
        verifyNoInteractions(mockSap);
    }
}
