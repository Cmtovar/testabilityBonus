package org.example;


import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FilterInvoiceTest {
    @Test
    void filterInvoiceTest() {
        // create real database and dao
        Database db = new Database();
        QueryInvoicesDAO dao = new QueryInvoicesDAO(db);

        // Insert test data into real database
        dao.save(new Invoice("Customer1", 50));
        dao.save(new Invoice("Customer2", 99)); // boundary at 100, seen in FilterInvoice.java
        dao.save(new Invoice("Customer3", 100));
        dao.save(new Invoice("Customer4", 150));

        // Create filterInvoice (uses real database)
        FilterInvoice filter = new FilterInvoice();

        // Call method being tested
        List<Invoice> result = filter.lowValueInvoices();

        // Assert results - expects that only invoices with value < 100 return
        assertEquals(2, result.size());

        for (Invoice invoice : result) {
            assertTrue(invoice.getValue() < 100);
        }

        // Clean up database
        dao.clear();
        db.close();
    }

    @Test
    void filterInvoiceStubbedTest() {
        // create mock dao
        QueryInvoicesDAO mockDao = mock(QueryInvoicesDAO.class);

        // create fake invoice data
        Invoice invoice1 = new Invoice("Customer1", 50);
        Invoice invoice2 = new Invoice("Customer2", 99); // boundary at 100, seen in FilterInvoice.java
        Invoice invoice3 = new Invoice("Customer3", 100);
        Invoice invoice4 = new Invoice("Customer4", 150);

        List<Invoice> fakeInvoices = new ArrayList<>();
        fakeInvoices.add(invoice1);
        fakeInvoices.add(invoice2);
        fakeInvoices.add(invoice3);

        // when dao.all() is called, return fake data - this stubs the mock
        when(mockDao.all()).thenReturn(fakeInvoices);

        // Create filterInvoice (with mockedDAO)
        FilterInvoice filter = new FilterInvoice(mockDao);

        // Call method being tested
        List<Invoice> result = filter.lowValueInvoices();

        // Assert results - expects that only invoices with value < 100 return
        assertEquals(2, result.size());

        for (Invoice invoice : result) {
            assertTrue(invoice.getValue() < 100);
        }

        assertTrue(result.contains(invoice1));
        assertTrue(result.contains(invoice2));
        assertTrue(result.contains(invoice3));

        // database not used - no need to clean
    }
}
