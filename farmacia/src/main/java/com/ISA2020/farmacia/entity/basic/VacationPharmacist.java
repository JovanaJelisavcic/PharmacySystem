package com.ISA2020.farmacia.entity.basic;

import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;

import com.ISA2020.farmacia.entity.users.Pharmacist;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
public class VacationPharmacist {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@JsonView(Views.VacationRequestsList.class)
	private Long vacationId;
	@ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "email", referencedColumnName = "email")
	@JsonView(Views.VacationRequestsList.class)
	private Pharmacist pharmacist;
	@JsonView(Views.VacationRequestsList.class)
 	@NotNull(message="Date is mandatory")
 	@Future(message="Date has to be in the future")
	private LocalDate beginning;
	@JsonView(Views.VacationRequestsList.class)
 	@NotNull(message="Date is mandatory")
 	@Future(message="Date has to be in the future")
	private LocalDate ending;
	@Enumerated(EnumType.STRING)
	@JsonView(Views.VacationRequestsList.class)
	@Column(nullable=false)
	private VacationStatus status;
	@JsonView(Views.VacationRequestsList.class)
	private String explanation;
	 @AssertTrue public boolean isValidRange() {
		    return beginning.isBefore(ending);
		  }
	public VacationPharmacist() {}
	public VacationPharmacist(Pharmacist pharmacist, LocalDate beginning, LocalDate ending,
			VacationStatus status, String explanation) {
		super();
		this.pharmacist = pharmacist;
		this.beginning = beginning;
		this.ending = ending;
		this.status = status;
		this.explanation = explanation;
	}
	public Pharmacist getPharmacist() {
		return pharmacist;
	}
	public void setPharmacist(Pharmacist pharmacist) {
		this.pharmacist = pharmacist;
	}
	public LocalDate getBeginning() {
		return beginning;
	}
	public void setBeginning(LocalDate beginning) {
		this.beginning = beginning;
	}
	public LocalDate getEnding() {
		return ending;
	}
	public void setEnding(LocalDate ending) {
		this.ending = ending;
	}
	public VacationStatus getStatus() {
		return status;
	}
	public void setStatus(VacationStatus status) {
		this.status = status;
	}
	public String getExplanation() {
		return explanation;
	}
	public void setExplanation(String explanation) {
		this.explanation = explanation;
	}
	
	
	
}
