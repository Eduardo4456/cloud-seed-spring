package com.project.cloudseed.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Plant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String species;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User users;

    @OneToOne(mappedBy = "plant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Schedule schedule;
}
