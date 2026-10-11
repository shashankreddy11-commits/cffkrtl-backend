package com.coffeekartel.service;

import com.coffeekartel.dto.request.ReservationRequest;
import com.coffeekartel.dto.response.ReservationResponse;
import com.coffeekartel.entity.Reservation;
import com.coffeekartel.exception.ResourceNotFoundException;
import com.coffeekartel.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {
        String confirmationCode = generateUniqueConfirmationCode();

        Reservation reservation = new Reservation(
                confirmationCode,
                request.getName(),
                request.getPhone(),
                request.getDate(),
                request.getTime(),
                request.getPartySize(),
                request.getSpecialRequests()
        );

        Reservation saved = reservationRepository.save(reservation);
        log.info("Reservation confirmed for {} with code {}", saved.getName(), saved.getConfirmationCode());

        return mapToResponse(saved, "Reservation successfully registered! We look forward to hosting you.");
    }

    @Transactional(readOnly = true)
    public ReservationResponse getByConfirmationCode(String confirmationCode) {
        Reservation reservation = reservationRepository.findByConfirmationCode(confirmationCode)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with reference: " + confirmationCode));
        return mapToResponse(reservation, null);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(r -> mapToResponse(r, null))
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservationResponse updateStatus(Long id, String status) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));

        reservation.setStatus(status.toUpperCase());
        Reservation updated = reservationRepository.save(reservation);
        log.info("Reservation {} status updated to {}", id, updated.getStatus());
        return mapToResponse(updated, "Reservation status updated to " + updated.getStatus());
    }

    private String generateUniqueConfirmationCode() {
        String code;
        do {
            int randomNum = 1000 + RANDOM.nextInt(9000); // 4 digit code
            code = "CK-RES-" + randomNum;
        } while (reservationRepository.findByConfirmationCode(code).isPresent());
        return code;
    }

    private ReservationResponse mapToResponse(Reservation r, String message) {
        return new ReservationResponse(
                r.getId(),
                r.getConfirmationCode(),
                r.getName(),
                r.getPhone(),
                r.getReservationDate(),
                r.getReservationTime(),
                r.getPartySize(),
                r.getSpecialRequests(),
                r.getStatus(),
                message,
                r.getCreatedAt()
        );
    }
}
