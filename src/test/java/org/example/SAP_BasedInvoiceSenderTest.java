package org.example;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

        // Call the method being tested - returns list of failed invoices (requirement 3)
        List<Invoice> failedInvoices = sender.sendLowValuedInvoices();

        // Verify that no failures occured
        assertEquals(0, failedInvoices.size());

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

        // Call the method being tested - returns list of failed invoices (requirement 3)
        List<Invoice> failedInvoices = sender.sendLowValuedInvoices();

        // Verify no failures (since no invoices are there to send)
        assertEquals(0, failedInvoices.size());

        // Verify sap.send() was never called
        // since there are no invoices, SAP is expected to not be sent anything
        // using verifyNoInteractions() to check that mockSap wasn't touched
        verifyNoInteractions(mockSap);
    }

    @Test
    void testThrowExceptionWhenBadInvoice() {
        // create mocks for both dependencies
        FilterInvoice mockFilter = mock(FilterInvoice.class);
        SAP mockSap = mock(SAP.class);

        // create test invoices
        Invoice invoice1 = new Invoice("Customer1", 50);
        Invoice invoice2 = new Invoice("Customer2", 60);
        Invoice invoice3 = new Invoice("Customer3", 70);

        List<Invoice> fakeInvoices = new ArrayList<>();
        fakeInvoices.add(invoice1);
        fakeInvoices.add(invoice2);
        fakeInvoices.add(invoice3);

        // stub filter to return invoices
        when(mockFilter.lowValueInvoices()).thenReturn(fakeInvoices);

        // stub sap.send() to throw exception for invoice2
        // doThrow().when() is the pattern to exceptions
        doThrow(new FailToSendSAPInvoiceException("SAP error"))
                .when(mockSap).send(invoice2);

        // create sender with mocked dependencies
        SAP_BasedInvoiceSender sender = new SAP_BasedInvoiceSender(mockFilter, mockSap);

        // Call the method - should NOT crash despite exception
        List<Invoice> failedInvoices = sender.sendLowValuedInvoices();

        // Verify only invoice2 failed
        assertEquals(1, failedInvoices.size());
        assertTrue(failedInvoices.contains(invoice2));

        // Vertify all invoices were attempted (didn't stop after failure)
        verify(mockSap).send(invoice1); // First invoice sends successfully
        verify(mockSap).send(invoice2); // Second invoice attempted - throws exception
        verify(mockSap).send(invoice3); // Third invoice still sends
    }
}
