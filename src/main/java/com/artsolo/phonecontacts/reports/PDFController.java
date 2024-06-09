package com.artsolo.phonecontacts.reports;

import com.artsolo.phonecontacts.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/download")
public class PDFController {

    private final PDFService pdfService;

    @GetMapping("/contacts-list")
    public ResponseEntity<byte[]> generateContactsListPdf(Principal currentUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) currentUser).getPrincipal();
        String htmlContent = pdfService.generateHtmlContent(user);
        byte[] pdfBytes = pdfService.generatePdfFromHtml(htmlContent);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + user.getUsername() + "_Contacts" + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

}
