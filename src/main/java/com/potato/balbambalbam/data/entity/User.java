package com.potato.balbambalbam.data.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity(name = "user")
@Getter
public class User {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "age", nullable = false)
    private Integer age;
    @Column(name = "gender", nullable = false)
    private Integer gender;
    @Column(name = "email", nullable = false)
    private String email;
    @Column(name = "voice", nullable = false)
    private Integer voice;

    public User() {
    }
}