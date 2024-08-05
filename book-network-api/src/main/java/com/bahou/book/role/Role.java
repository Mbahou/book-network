package com.bahou.book.role;

import com.bahou.book.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Role {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    private List<User> users;


    @CreatedDate
    @Column(nullable = false,updatable = false)
    private LocalDateTime createdDate;
    @CreatedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;

}
