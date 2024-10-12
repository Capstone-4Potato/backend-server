package com.potato.balbambalbam.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "level")
@Getter
@Setter
public class Level {

    @Id
    @Column(name = "level_id")
    private long levelId;
    @Column(name = "level")
    private String level;
    @Column(name = "level_experience")
    private long levelExperience;

    public Level(){
    }
}
