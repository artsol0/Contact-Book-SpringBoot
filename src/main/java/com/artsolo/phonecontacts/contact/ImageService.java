package com.artsolo.phonecontacts.contact;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class ImageService {

    private final String uploadDirectory = "../Pictures/user-images/";

    public String saveImageToUserStorage(String userName, MultipartFile imageFile) {
        try {
            String uniqueFileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();

            Path userDirectory = Path.of(uploadDirectory + userName);
            Path imagePath = userDirectory.resolve(uniqueFileName);

            if (!Files.exists(userDirectory)) {
                Files.createDirectories(userDirectory);
            }

            Files.copy(imageFile.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);

            return imagePath.toString();
        } catch (IOException e) {
            log.error("Error saving new contact image: {}", e.getMessage());
            return null;
        }
    }

    public byte[] getImage(String imagePath) {
        try {
            Path path = Path.of(imagePath);
            if (Files.exists(path)) {
                return Files.readAllBytes(path);
            }
            return null;
        } catch (IOException e) {
            log.error("Error reading image bytes: {}", e.getMessage());
            return null;
        }
    }

    public void deleteImage(String imagePath) {
        try {
            Files.deleteIfExists(Path.of(imagePath));
        } catch (IOException e) {
            log.error("Error deleting image: {}", e.getMessage());
        }
    }

    public void deleteUserStorage(String userName) {
        try {
            Files.deleteIfExists(Path.of(uploadDirectory + userName));
        } catch (IOException e) {
            log.error("Error deleting user storage: {}", e.getMessage());
        }
    }

}
