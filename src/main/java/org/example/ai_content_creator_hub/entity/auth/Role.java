package org.example.ai_content_creator_hub.entity.auth;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Table(name = "roles")  // Changed to avoid using reserved keywords.
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private RoleName name;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    @ToString.Exclude  // Avoid recursive issues in `toString()`
    private List<User> users = new ArrayList<>();

    public Role() {}

    public Role(RoleName name) {
        this.name = name;
    }
}
