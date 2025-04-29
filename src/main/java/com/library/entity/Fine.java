package com.library.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "fine")
@Getter
@Setter
public class Fine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fineId;

    @OneToOne
    @JoinColumn(name = "borrow_id", nullable = false)
    private Borrow borrow;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private double amount;

    private boolean paidStatus;

    /*
    public Long getFineId() { return fineId; }
    public void setFineId(Long fineId) { this.fineId = fineId; }
    public Borrow getBorrow() { return borrow; }
    public void setBorrow(Borrow borrow) { this.borrow = borrow; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public boolean isPaidStatus() { return paidStatus; }
    public void setPaidStatus(boolean paidStatus) { this.paidStatus = paidStatus; }*/
}