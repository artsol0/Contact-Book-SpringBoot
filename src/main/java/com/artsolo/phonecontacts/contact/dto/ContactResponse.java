package com.artsolo.phonecontacts.contact.dto;

import com.artsolo.phonecontacts.email.EmailAddressResponse;
import com.artsolo.phonecontacts.phone.PhoneNumberResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactResponse {
    private Long id;
    private String name;
    private List<EmailAddressResponse> emails;
    private List<PhoneNumberResponse> phones;
    private byte[] image;
}
