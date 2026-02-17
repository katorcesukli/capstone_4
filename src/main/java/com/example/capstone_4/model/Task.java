package com.example.capstone_4.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name ="tasks")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer task_id;

    @Column(nullable = false)
    private String task_name;

    @Column(nullable = false)
    private String task_description;

    @Column(nullable = false)
    private String task_status;

    @Column(nullable = false)
    private LocalDate task_date;
}
