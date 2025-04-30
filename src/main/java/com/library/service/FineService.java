package com.library.service;

import com.library.entity.Fine;
import com.library.entity.Borrow;
import com.library.repository.FineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class FineService {

    @Autowired
    private FineRepository fineRepository;

    public Fine saveFine(Fine fine) {
        return fineRepository.save(fine);
    }

    public List<Fine> getAllFines() {
        return fineRepository.findAll();
    }

    public List<Fine> getFinesByUser(Long userId) {
        return fineRepository.findByUserUserId(userId);
    }

    public Fine markFineAsPaid(Long fineId) {
        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(() -> new RuntimeException("Fine not found with id: " + fineId));
        fine.setPaid(true);
        return fineRepository.save(fine);
    }

    public Fine calculateFineForBorrow(Borrow borrow) {
        if (borrow.getReturnDate() == null || borrow.getReturnDate().isBefore(borrow.getBorrowDate().plusDays(14))) {
            return null; // Not overdue
        }

        long daysOverdue = ChronoUnit.DAYS.between(borrow.getBorrowDate().plusDays(14), borrow.getReturnDate());
        if (daysOverdue <= 0) {
            return null; // Not overdue
        }

        // Fine: $0.50 per day overdue
        BigDecimal fineAmount = BigDecimal.valueOf(daysOverdue * 0.50);

        Fine fine = new Fine();
        fine.setUser(borrow.getUser());
        fine.setBorrow(borrow);
        fine.setAmount(fineAmount);
        fine.setPaid(false);
        return saveFine(fine);
    }
}