package com.example.capstone_4.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name ="tasks")
@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, name ="task_id")
    private String taskId; //serves to connect to the account

    @Column(nullable = false, name ="task_name")
    private String taskName;

    @Column(nullable = false, name ="task_description")
    private String taskDescription;

    @Column(nullable = false, name ="task_status")
    private String taskStatus;

    @Column(nullable = false, name ="task_date")
    private LocalDate taskDate;


}
