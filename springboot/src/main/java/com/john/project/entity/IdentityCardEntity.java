package com.john.project.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

import jakarta.persistence.*;

@Entity
@Getter
@Setter
@Accessors(chain = true)
public class IdentityCardEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Date createDate;

    @Column(nullable = false)
    private Date updateDate;

    /**
     * @see com.john.project.enums.SystemPermissionEnum
     * <p>
     * ANONYMOUS
     * RESIDENT
     * IMMIGRANT
     * CITIZEN
     */
    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, optional = false)
    private PermissionEntity permission;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, optional = false)
    private UserEntity user;

    /**
     * governance region, Above the governance region is the nation.
     */
    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, optional = false)
    private OrganizeEntity organize;

}
