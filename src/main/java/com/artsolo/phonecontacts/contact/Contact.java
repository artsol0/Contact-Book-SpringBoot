package com.artsolo.phonecontacts.contact;

import com.artsolo.phonecontacts.email.EmailAddress;
import com.artsolo.phonecontacts.phone.PhoneNumber;
import com.artsolo.phonecontacts.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Column(unique = true, length = 50)
    private String name;

    @OneToMany(mappedBy = "contact", orphanRemoval = true, cascade = CascadeType.PERSIST)
    private List<EmailAddress> emailAddresses = new ArrayList<>();

    @OneToMany(mappedBy = "contact", orphanRemoval = true, cascade = CascadeType.PERSIST)
    private List<PhoneNumber> phoneNumbers = new ArrayList<>();

    @Column(name = "contact_image")
    private String imagePath;
}
