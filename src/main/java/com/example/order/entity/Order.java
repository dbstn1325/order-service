package com.example.order.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name="orders")
public class Order implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String productId;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private int unitPrice;

    @Column(nullable = false)
    private int totalPrice;

    @Column(nullable = false)
    private String orderId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

}
