package com.artsolo.phonecontacts;

import com.artsolo.phonecontacts.configurations.JwtConfig;
import com.artsolo.phonecontacts.dto.ContactDto;
import com.artsolo.phonecontacts.models.Contact;
import com.artsolo.phonecontacts.models.EmailAddress;
import com.artsolo.phonecontacts.models.User;
import com.artsolo.phonecontacts.models.UserPrincipal;
import com.artsolo.phonecontacts.repositoryes.ContactRepository;
import com.artsolo.phonecontacts.repositoryes.EmailAddressRepository;
import com.artsolo.phonecontacts.repositoryes.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.transaction.Transactional;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ContactControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private EmailAddressRepository emailAddressRepository;

    private String generateToken(String username) {
        UserPrincipal userPrincipal = UserPrincipal.create(userRepository.findByUsername(username));
        Claims claims = Jwts.claims().setSubject(userPrincipal.getUsername());
        claims.put("userId", userPrincipal.getId());
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtConfig.TOKEN_EXPIRATION_MS);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, jwtConfig.SECRET_KEY)
                .compact();
    }

    @Before
    public void setUp() {
        // Create a test user
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password"));
        userRepository.save(user);
    }

    @After
    public void tearDown() {
        // Clean up the database after each test
        contactRepository.deleteAll();
        userRepository.deleteAll();
        contactRepository.flush(); // Add this line to ensure the delete operation is executed immediately
    }

    @Test
    @Transactional
    public void testCreateContact_Successful() throws Exception {
        // Generate a JWT token for the test user
        String token = generateToken("testuser");

        // Create a contact DTO
        List<String> emails = Collections.singletonList("abdwjf@gmail.com");
        List<String> phones = Collections.singletonList("+38093734");
        ContactDto contactDto = new ContactDto("test", emails, phones);

        // Send a POST request to create a contact
        mockMvc.perform(MockMvcRequestBuilders.post("/api/contact/create")
                        .content(asJsonString(contactDto))
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Contact created successfully"));

        // Verify that the contact is actually created in the database
        List<Contact> contactList = contactRepository.findAll();
        Assert.assertEquals(1, contactList.size());
        Contact contact = contactList.get(0);
        Assert.assertEquals("test", contact.getName());
        Assert.assertEquals(emails, contact.getEmails());
        Assert.assertEquals(phones, contact.getPhoneNumbersList());
    }

    @Test
    public void testRenameContact_Successful() throws Exception {
        // Generate a JWT token for the test user
        String token = generateToken("testuser");

        // Create a contact DTO
        List<String> emails = Collections.singletonList("abdwjf@gmail.com");
        List<String> phones = Collections.singletonList("+38093734");
        ContactDto contactDto = new ContactDto("test", emails, phones);

        // Send a POST request to create a contact
        mockMvc.perform(MockMvcRequestBuilders.post("/api/contact/create")
                        .content(asJsonString(contactDto))
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Contact created successfully"));

        // Retrieve the created contact from the database
        List<Contact> contactList = contactRepository.findAll();
        Assert.assertEquals(1, contactList.size());
        Contact contact = contactList.get(0);

        // Prepare the request body for renaming the contact
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("newName", "new name");

        // Send a PATCH request to rename the contact
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/contact/rename/{contactId}", contact.getId())
                        .content(asJsonString(requestBody))
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Contact renamed successfully"));

        // Retrieve the renamed contact from the database
        Contact renamedContact = contactRepository.findById(contact.getId()).orElse(null);
        Assert.assertNotNull(renamedContact);
        Assert.assertEquals("new name", renamedContact.getName());
    }

    @Test
    public void testUpdateEmailAddress_Failure() throws Exception {
        // Generate a JWT token for the test user
        String token = generateToken("testuser");

        // Create a contact DTO
        List<String> emails = Collections.singletonList("abdwjf@gmail.com");
        List<String> phones = Collections.singletonList("+38093734");
        ContactDto contactDto = new ContactDto("test", emails, phones);

        // Send a POST request to create a contact
        mockMvc.perform(MockMvcRequestBuilders.post("/api/contact/create")
                        .content(asJsonString(contactDto))
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Contact created successfully"));

        // Retrieve the created contact from the database
        List<Contact> contactList = contactRepository.findAll();
        Assert.assertEquals(1, contactList.size());
        Contact contact = contactList.get(0);

        // Retrieve the email address from the contact
        List<EmailAddress> emailAddressList = contact.getEmailAddresses();
        Assert.assertEquals(1, emailAddressList.size());
        EmailAddress existingEmailAddress = emailAddressList.get(0);

        // Prepare the request body with a new invalid email address
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("newEmail", "invalid_email");

        // Send a PATCH request to update the email address with an invalid email
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/contact/update-email/{emailId}", existingEmailAddress.getId())
                        .content(asJsonString(requestBody))
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Failed to update email"));

        // Retrieve the email address from the database and verify that it has not been updated
        EmailAddress updatedEmailAddress = emailAddressRepository.findById(existingEmailAddress.getId()).orElse(null);
        Assert.assertNotNull(updatedEmailAddress);
        Assert.assertEquals(existingEmailAddress.getEmail(), updatedEmailAddress.getEmail());
    }

    @Test
    public void testDeleteContact_NonExistentId() throws Exception {
        // Generate a JWT token for the test user
        String token = generateToken("testuser");

        // Create a contact DTO
        List<String> emails = Collections.singletonList("abdwjf@gmail.com");
        List<String> phones = Collections.singletonList("+38093734");
        ContactDto contactDto = new ContactDto("test", emails, phones);

        // Send a POST request to create a contact
        mockMvc.perform(MockMvcRequestBuilders.post("/api/contact/create")
                        .content(asJsonString(contactDto))
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Contact created successfully"));

        // Retrieve the created contact from the database
        List<Contact> contactList = contactRepository.findAll();
        Assert.assertEquals(1, contactList.size());
        Contact contact = contactList.get(0);

        // Delete the contact with a non-existent contact ID
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/contact/delete/{contactId}", 9999L)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Failed to delete contact!"));

        // Verify that the contact still exists in the database
        List<Contact> updatedContactList = contactRepository.findAll();
        Assert.assertEquals(1, updatedContactList.size());
        Contact updatedContact = updatedContactList.get(0);
        Assert.assertEquals(contact.getId(), updatedContact.getId());
        Assert.assertEquals(contact.getName(), updatedContact.getName());
        Assert.assertEquals(contact.getEmails(), updatedContact.getEmails());
        Assert.assertEquals(contact.getPhoneNumbersList(), updatedContact.getPhoneNumbersList());
    }

    private String asJsonString(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }
}
