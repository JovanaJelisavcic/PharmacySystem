package com.ISA2020.farmacia.controller;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ISA2020.farmacia.entity.basic.Farmacy;
import com.ISA2020.farmacia.entity.basic.Views;
import com.ISA2020.farmacia.entity.users.Pharmacist;
import com.ISA2020.farmacia.repository.FarmacyAdminRepository;
import com.ISA2020.farmacia.repository.PharmacistRepository;
import com.ISA2020.farmacia.security.JwtUtils;
import com.ISA2020.farmacia.util.FilteringUtil;
import com.fasterxml.jackson.annotation.JsonView;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;

@RestController
@RequestMapping("/pharmacist")
public class PharmacistController {
		
	@Autowired
	JwtUtils jwtUtils;
	@Autowired
	FarmacyAdminRepository farmAdminRepo;
	@Autowired
	PharmacistRepository pharmacistRepo;
	@Autowired
	FilteringUtil filteringUtil;
	
	@JsonView(Views.SimpleUser.class)
	@GetMapping("/all")
	@PreAuthorize("hasAuthority('FARMACY_ADMIN')")
	public ResponseEntity<?> pharmacyist(@RequestHeader("Authorization") String token) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, IllegalArgumentException, UnsupportedEncodingException {	
		String username =jwtUtils.getUserNameFromJwtToken(token.substring(6, token.length()).strip());
		Farmacy farmacy =  farmAdminRepo.findById(username).get().getFarmacy();
		List<Pharmacist> list = pharmacistRepo.findByFarmacyId(farmacy.getId());
		if(list.isEmpty() ) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		 return new ResponseEntity<>(list, HttpStatus.OK);
	}
	
	@JsonView(Views.SearchPharmacistsForPatient.class)
	@GetMapping("")
	@PreAuthorize("hasAuthority('PATIENT')")
	public ResponseEntity<?> pharmacyistAll()  {	
		List<Pharmacist> list = pharmacistRepo.findAll();
		if(list.isEmpty() ) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		 return new ResponseEntity<>(filteringUtil.filterAdress(list), HttpStatus.OK);
	}
	
	
	@PostMapping("/add")
	@PreAuthorize("hasAuthority('FARMACY_ADMIN')")
	public ResponseEntity<?> addPharmacist(@RequestHeader("Authorization") String token, @Valid @RequestBody Pharmacist pharmacist) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, IllegalArgumentException, UnsupportedEncodingException {	
		if(pharmacistRepo.findById(pharmacist.getEmail()).isPresent()) return new ResponseEntity<>(HttpStatus.CONFLICT);
		String username =jwtUtils.getUserNameFromJwtToken(token.substring(6, token.length()).strip());
		Farmacy farmacy =  farmAdminRepo.findById(username).get().getFarmacy();
		pharmacist.setFarmacy(farmacy);
		pharmacist.setStars(0);
		pharmacistRepo.save(pharmacist);
		return ResponseEntity.ok().build();
		 
	}
	
	
	@DeleteMapping("/delete/{email}")
	@PreAuthorize("hasAuthority('FARMACY_ADMIN')")
	public ResponseEntity<?> deletePharma(@RequestHeader("Authorization") String token, @PathVariable String email) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, IllegalArgumentException, UnsupportedEncodingException {		
		Optional<Pharmacist> pharmacist = pharmacistRepo.findById(email);
		if(pharmacist.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		String username =jwtUtils.getUserNameFromJwtToken(token.substring(6, token.length()).strip());
		Farmacy farmacy =  farmAdminRepo.findById(username).get().getFarmacy();
		
		if(!pharmacist.get().getFarmacy().equals(farmacy)) return new ResponseEntity<>(HttpStatus.CONFLICT);
		if(pharmacist.get().getCounselings().stream().anyMatch(p->!p.isCanceled() && !p.isShowUp() && p.getDateTime().isAfter(LocalDateTime.now()))) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		pharmacistRepo.delete(pharmacist.get());
		return new ResponseEntity<>(HttpStatus.OK);
		 
	}
	
	@JsonView(Views.VerySimpleUser.class)
	@GetMapping("/search/{parametar}")
	@PreAuthorize("hasAuthority('FARMACY_ADMIN')")
	public ResponseEntity<Object> searchFarmacyPharmacist(@RequestHeader("Authorization") String token,@PathVariable String parametar) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, IllegalArgumentException, UnsupportedEncodingException {	
		 StringBuilder sb = new StringBuilder(parametar.concat("%"));
		 sb.insert(0,"%");
		 List<Pharmacist> pharmacists = pharmacistRepo.findByNameLikeIgnoreCaseOrSurnameLikeIgnoreCase(sb.toString(),sb.toString());
		if(pharmacists.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		String username =jwtUtils.getUserNameFromJwtToken(token.substring(6, token.length()).strip());
		Farmacy farmacy = farmAdminRepo.findById(username).get().getFarmacy();
		
		pharmacists.removeIf(pharm -> !pharm.getFarmacy().equals(farmacy));
		return new ResponseEntity<Object>(pharmacists, HttpStatus.OK);
		
		 
	}
	
	
	@JsonView(Views.VerySimpleUser.class)
	@GetMapping("/farmacy/{id}")
	@PreAuthorize("hasAuthority('PATIENT')")
	public ResponseEntity<?> pharmacyistFromFarma(@PathVariable String id)  {	
		List<Pharmacist> list = pharmacistRepo.findByFarmacyId(id);
		if(list.isEmpty() ) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		 return new ResponseEntity<>(list, HttpStatus.OK);
	}
	
	
	
	
}
