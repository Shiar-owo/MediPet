package com.medipet.medipet.pet.infrastructure.outbound.persistence.entity;

import com.medipet.medipet.shared.domain.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "pets")
public class PetEntity extends Auditable {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String species;

    @Column(length = 100)
    private String breed;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(length = 10)
    private String sex;

    @Column(length = 50)
    private String color;

    private BigDecimal weight;

    @Column(name = "microchip_id", unique = true, length = 50)
    private String microchipId;

    @Column(name = "tattoo_number", length = 50)
    private String tattooNumber;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "client_id", nullable = false, columnDefinition = "UUID")
    private UUID clientId;
}
