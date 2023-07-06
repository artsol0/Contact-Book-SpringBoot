package com.artsolo.phonecontacts.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "contacts")
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contact_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(unique = true)
    private String name;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "contact")
    private List<EmailAddress> emailAddresses = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, mappedBy = "contact")
    private List<PhoneNumber> phoneNumbers = new ArrayList<>();

    @Column(name = "contact_image", columnDefinition = "LONGBLOB")
    private byte[] image;

    public List<String> getEmails() {
        List<String> emailList = new ArrayList<>();
        for (EmailAddress emailAddress : emailAddresses) {
            emailList.add(emailAddress.getEmail());
        }
        return emailList;
    }

    public List<String> getPhoneNumbersList() {
        List<String> phoneNumberList = new ArrayList<>();
        for (PhoneNumber phoneNumber : phoneNumbers) {
            phoneNumberList.add(phoneNumber.getPhoneNumber());
        }
        return phoneNumberList;
    }
}
