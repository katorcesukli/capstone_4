package com.example.capstone_4.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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


    @ManyToOne
    @JoinColumn(name = "account_id" ,referencedColumnName = "account_id")
    private Account taskId;

    @Column(nullable = false, name ="task_name")
    private String taskName;

    @Column(nullable = false, name ="task_description")
    private String taskDescription;

    @Column(nullable = false, name ="task_status")
    private String taskStatus;

    @Column(nullable = false, name ="task_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate taskDate;
}
