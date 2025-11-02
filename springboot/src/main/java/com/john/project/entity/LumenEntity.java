package com.john.project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Accessors(chain = true)
public class LumenEntity {

    @Id
    private String id;

    @Column(nullable = false, precision = 38, scale = 9)
    private BigDecimal money;

}
