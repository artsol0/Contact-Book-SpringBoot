package com.artsolo.phonecontacts.reports;

import com.artsolo.phonecontacts.contact.ContactService;
import com.artsolo.phonecontacts.contact.dto.ContactResponse;
import com.artsolo.phonecontacts.user.User;
import com.itextpdf.html2pdf.HtmlConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PDFService {

    private final ContactService contactService;
    private final TemplateEngine templateEngine;

    public String generateHtmlContent(User user) {
        List<ContactResponse> contacts = contactService.getUserContacts(user);
        Context context = new Context();
        context.setVariable("contacts", contacts);
        context.setVariable("userName", user.getUsername());
        return templateEngine.process("contacts-list", context);
    }

    public byte[] generatePdfFromHtml(String htmlContent) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HtmlConverter.convertToPdf(htmlContent, outputStream);
        return outputStream.toByteArray();
    }

}
