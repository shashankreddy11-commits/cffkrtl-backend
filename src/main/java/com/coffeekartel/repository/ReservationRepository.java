package com.coffeekartel.repository;

import com.coffeekartel.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByConfirmationCode(String confirmationCode);
    List<Reservation> findAllByOrderByCreatedAtDesc();
}
