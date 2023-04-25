package com.SpringMaven.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class Account {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;
    private String username;
    private String password;
    private Integer status;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles;
}
