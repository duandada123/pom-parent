package com.sinogale.jpa.support;

import com.sinogale.jpa.converter.InstantLongConverter;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.AbstractAggregateRoot;

import javax.persistence.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@MappedSuperclass
public abstract class BaseJpaAggregate extends AbstractAggregateRoot<BaseJpaAggregate> {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(
            name = "id"
    )
    private Long id;
    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    @Convert(
            converter = InstantLongConverter.class
    )
    private Instant createdAt;
    @Column(
            name = "updated_at",
            nullable = false
    )
    @Convert(
            converter = InstantLongConverter.class
    )
    private Instant updatedAt;
    @Version
    @Column(
            name = "version"
    )
    private Integer version;

    @PrePersist
    public void prePersist() {
        this.setCreatedAt(Instant.now());
        this.setUpdatedAt(Instant.now());
    }

    @PreUpdate
    public void preUpdate() {
        this.setUpdatedAt(Instant.now());
    }



}
