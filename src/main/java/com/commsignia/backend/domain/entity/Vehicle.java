package com.commsignia.backend.domain.entity;

import com.commsignia.backend.domain.entity.Notification;
import com.commsignia.backend.domain.entity.Position;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "vehicle")
public class Vehicle {

    @Id
    private String id;

    @Column(name = "created_at")
    private LocalDateTime created_at;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "notification_id")
    private Set<Notification> notifications;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "poistion_id")
    private Set<Position> positions;
}
