package com.yourcompany.invoicesystem.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.yourcompany.invoicesystem.entity.Invoice;
import com.yourcompany.invoicesystem.entity.InvoiceItem;
import com.yourcompany.invoicesystem.exception.ResourceNotFoundException;
import com.yourcompany.invoicesystem.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class PdfService {

    private final InvoiceRepository invoiceRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public byte[] generateInvoicePdf(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + invoiceId));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(baos);
                PdfDocument pdfDoc = new PdfDocument(writer);
                Document document = new Document(pdfDoc)) {

            // Header
            document.add(new Paragraph("Invoice Management System")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("Invoice #" + invoice.getInvoiceNumber())
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("Date: " + invoice.getInvoiceDate().format(DATE_FMT))
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("\n"));

            // Customer details
            document.add(new Paragraph("Bill To:").setBold());
            document.add(new Paragraph(invoice.getCustomer().getName()));
            if (invoice.getCustomer().getEmail() != null) {
                document.add(new Paragraph(invoice.getCustomer().getEmail()));
            }
            if (invoice.getCustomer().getPhone() != null) {
                document.add(new Paragraph(invoice.getCustomer().getPhone()));
            }
            if (invoice.getCustomer().getAddress() != null) {
                document.add(new Paragraph(invoice.getCustomer().getAddress()));
            }

            document.add(new Paragraph("\n"));

            // Item table
            Table table = new Table(UnitValue.createPercentArray(new float[] { 4, 1, 2, 2, 2 }))
                    .useAllAvailableWidth();

            addHeaderCell(table, "Product");
            addHeaderCell(table, "Qty");
            addHeaderCell(table, "Price");
            addHeaderCell(table, "GST %");
            addHeaderCell(table, "Line Total");

            for (InvoiceItem item : invoice.getItems()) {
                table.addCell(new Cell().add(new Paragraph(item.getProduct().getName())));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getQuantity()))));
                table.addCell(new Cell().add(new Paragraph(item.getPriceAtSale().toPlainString())));
                table.addCell(new Cell().add(new Paragraph(item.getProduct().getGstPercent().toPlainString() + "%")));
                table.addCell(new Cell().add(new Paragraph(
                        item.getPriceAtSale().multiply(java.math.BigDecimal.valueOf(item.getQuantity()))
                                .toPlainString())));
            }

            document.add(table);
            document.add(new Paragraph("\n"));

            // Totals
            Table totals = new Table(UnitValue.createPercentArray(new float[] { 3, 1 }))
                    .useAllAvailableWidth();

            totals.addCell(rightCell("Discount:"));
            totals.addCell(new Cell().add(new Paragraph(invoice.getDiscountPercent().toPlainString() + "%")));

            totals.addCell(rightCell("GST Amount:"));
            totals.addCell(new Cell().add(new Paragraph(invoice.getGstAmount().toPlainString())));

            totals.addCell(rightCell("Grand Total:").setBold());
            totals.addCell(new Cell().add(new Paragraph(invoice.getTotalAmount().toPlainString()).setBold()));

            document.add(totals);
        }

        catch (java.io.IOException e) {
            throw new RuntimeException("Failed to generate PDF for invoice " + invoiceId, e);
        }

        return baos.toByteArray();
    }

    private void addHeaderCell(Table table, String text) {
        table.addHeaderCell(new Cell()
                .add(new Paragraph(text).setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY));
    }

    private Cell rightCell(String text) {
        return new Cell().add(new Paragraph(text)).setTextAlignment(TextAlignment.RIGHT);
    }
}
