package com.example.ContactList.Service;

import com.example.ContactList.Model.Contact;
import com.example.ContactList.Repo.ContactRepo;
import jakarta.servlet.Servlet;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.example.ContactList.Constant.Constants.PHOTO_DIRECTORY;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Slf4j
@Service
@Transactional(rollbackOn = Exception.class)
public class ContactService {

    @Autowired
    private ContactRepo repo;

    public Page<Contact> getAllContacts(int page , int size) {
        return repo.findAll(PageRequest.of(page , size , Sort.by("name")));
    }

    public Contact getContact(String  id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException( "contact not found"));
    }

    public Contact saveContact(Contact contact){
        return repo.save(contact);
    }

    public void deleteContact(Contact contact){
        repo.delete(contact);
    }

    public String uploadPhoto(String id , MultipartFile file){
        log.info("Saving picture for user ID : {}" , id);
        Contact contact = getContact(id);
        String photoUrl = photoFunction.apply(id,file);
        contact.setPhotoUrl(photoUrl);
        repo.save(contact);
        return photoUrl;
    }

    private final Function<String,String> fileExtension = filename -> Optional.of(filename)
            .filter(name -> name.contains("."))
            .map(name-> "." + name.substring(filename.lastIndexOf(".") + 1))
            .orElse(".png");



    private final BiFunction<String , MultipartFile ,String> photoFunction = (id , image ) -> {
        try{
            String filename = id + fileExtension.apply(image.getOriginalFilename());
            Path fileLocation = Paths.get(PHOTO_DIRECTORY).toAbsolutePath().normalize();

            if(!Files.exists((fileLocation))) { Files.createDirectories(fileLocation) ;}

            Files.copy(image.getInputStream(), fileLocation.resolve(filename) , REPLACE_EXISTING );

            return ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/contacts/image" + filename).toUriString();
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to save image");
        }
    };
































}
