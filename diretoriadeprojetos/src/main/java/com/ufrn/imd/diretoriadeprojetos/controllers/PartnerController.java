package com.ufrn.imd.diretoriadeprojetos.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ufrn.imd.diretoriadeprojetos.annotations.IsGuest;
import com.ufrn.imd.diretoriadeprojetos.annotations.IsUser;
import com.ufrn.imd.diretoriadeprojetos.dtos.response.ParceiroResponse;
import com.ufrn.imd.diretoriadeprojetos.models.Partner;
import com.ufrn.imd.diretoriadeprojetos.services.PartnerService;

@RestController
@RequestMapping("/parceiros")
public class PartnerController {
    @Autowired
    private PartnerService partnerService;

    @GetMapping
    public List<ParceiroResponse> findAll() {
        return partnerService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Partner> findById(@PathVariable UUID id) {
        return partnerService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @IsUser
    @PostMapping
    public ResponseEntity<Partner> create(@RequestBody Partner partner) {
        return ResponseEntity.status(HttpStatus.CREATED).body(partnerService.save(partner));
    }

    @IsUser
    @PutMapping("/{id}")
    public ResponseEntity<Partner> update(@PathVariable UUID id, @RequestBody Partner partner) {

        Partner updatedPartner = partnerService.update(id, partner);
        return ResponseEntity.ok(updatedPartner);

    }

    @IsUser
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        partnerService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
