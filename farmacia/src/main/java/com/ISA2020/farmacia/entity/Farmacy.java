package com.ISA2020.farmacia.entity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Farmacy {

	
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
	private String farmacyId;
	@Column(nullable=false)
	@JsonView(Views.VerySimpleFarmacy.class)
	private String name;
	 @Column(nullable=false)
	 @JsonView(Views.VerySimpleFarmacy.class)
	private String adress;
	 @JsonView(Views.VerySimpleFarmacy.class)
	 @Column(columnDefinition="Decimal(2,1)")
	 @JsonInclude(JsonInclude.Include.NON_NULL)
	private float stars;
	 @JsonView(Views.SimpleFarmacy.class)
	 @JsonInclude(JsonInclude.Include.NON_NULL)
	 private String description;
	 
	@OneToMany(mappedBy = "farmacy", fetch = FetchType.LAZY,
		            cascade = CascadeType.ALL, targetEntity = Price.class)
	@JsonView(Views.SemiDetailedFarmacy.class)
	 private List<Price> prices; 
	 
	@ManyToMany(mappedBy = "farmacies")
	@JsonView(Views.VeryDetailedFarmacy.class)
	private List<Drug> drugs;
	 
	
	public Farmacy() {}
	
	public Farmacy(String name, String adress, float stars, String description) {
		super();
		this.name = name;
		this.adress = adress;
		this.stars = stars;
		this.description = description;
	}
	public String getId() {
		return farmacyId;
	}
	public void setId(String id) {
		this.farmacyId = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAdress() {
		return adress;
	}
	public void setAdress(String adress) {
		this.adress = adress;
	}
	public float getStars() {
		return stars;
	}
	public void setStars(float stars) {
		this.stars = stars;
	}

	public List<Price> getPrices() {
		return prices;
	}

	public void setPrices(List<Price> prices) {
		this.prices = prices;
	}

	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public List<Drug> getDrugs() {
		return drugs;
	}

	public void setDrugs(List<Drug> drugs) {
		this.drugs = drugs;
	}

	public boolean addPrice(Price price) {
		boolean check = true;
		for(Price p : prices) {
			if(p.getDrug().equals(price.getDrug())) {
			
			if(!((price.getStandsFrom().isBefore(p.getStandsFrom()) && price.getStandsUntill().isBefore(p.getStandsFrom()))||(price.getStandsFrom().isAfter(p.getStandsUntill())&&price.getStandsUntill().isAfter(p.getStandsUntill()))))
						{check=false;}
		
			}}
		if(check) {
		prices.add(price);
		return true;
		}
		else return false;
	}


	
	
	
	
}
