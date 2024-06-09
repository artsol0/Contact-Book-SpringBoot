package com.artsolo.phonecontacts.contact.dto;

import com.artsolo.phonecontacts.validators.EmailCollection;
import com.artsolo.phonecontacts.validators.PhoneCollection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddContactRequest {
    @NotBlank(message = "Contact name is mandatory")
    @NotNull(message = "Contact name can't be null")
    @Size(max = 50, message = "Contact name cannot contain more than 50 characters")
    private String name;

    @EmailCollection
    private List<String> emails;

    @PhoneCollection
    @NotEmpty(message = "Contact must at list contain one phone number")
    private List<String> phones;

    private MultipartFile image;
}
