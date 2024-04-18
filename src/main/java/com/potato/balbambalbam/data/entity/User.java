package com.potato.balbambalbam.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "user")
@Getter
@Setter
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
    private Byte gender; // 0 : 여성, 1 : 남성
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "voice", nullable = false)
    private Integer voice;

    public User() {
    }
}
