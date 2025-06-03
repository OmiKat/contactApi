package com.example.ContactList.Controller;

import com.example.ContactList.Model.Contact;
import com.example.ContactList.Service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contacts")
public class ContactController {

    @Autowired
    private ContactService service;


    @GetMapping
    public ResponseEntity<Page<Contact>> getContacts(@RequestParam(value = "page",defaultValue = "0") int page,
                                                     @RequestParam(value = "size",defaultValue = "10") int size) {
        return ResponseEntity.ok().body(service.getAllContacts(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contact> getContact(@PathVariable(value = "id") String id){
        Contact contact = service.getContact(id);
        if(contact != null){ return new ResponseEntity<>(contact, HttpStatus.ACCEPTED);}
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping
    public ResponseEntity<Contact> createContact(@RequestBody Contact contact) {
        return ResponseEntity.ok().body(service.saveContact(contact));
    }



}
