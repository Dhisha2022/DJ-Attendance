package com.DJACompany.djattendance;

import java.math.BigDecimal;
import java.util.Date;
public class Invoices {
    public InvoiceData[] getInvoices() {
        InvoiceData[] data = new InvoiceData[5];
        for(int i = 0; i < 5; i ++) {
            InvoiceData row = new InvoiceData();
            row.id = (i+1);
            row.invoiceNumber = row.id;
            row.amountDue = BigDecimal.valueOf(20.00 * i);
            row.invoiceAmount = BigDecimal.valueOf(120.00 * (i+1));
            row.invoiceDate = new Date();
            row.customerName = "Yuvan Shankar Raja";
            row.customerAddress = "1112, Chennai, India";
            data[i] = row;
        }
        return data;
    }
}
