package com.example.dailyschedule.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class Schedule {
    @Setter private Long id;
    private String task;
    private String password;
    private String memberName;
    @Setter private LocalDateTime createdAt;
    @Setter private LocalDateTime updatedAt;

    public Schedule(String task, String password, String memberName) {
        this.task = task;
        this.password = password;
        this.memberName = memberName;
    }

    public Schedule(Long id, String task, String memberName, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.task = task;
        this.memberName = memberName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void update(String task, String memberName) {
        this.task = task;
        this.memberName = memberName;
    }
}
