package org.example;


import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
