package com.ykanji.reserveflow.service;

import com.ykanji.reserveflow.dto.ReservationCreateRequest;
import com.ykanji.reserveflow.dto.ReservationListDto;
import com.ykanji.reserveflow.entity.Customer;
import com.ykanji.reserveflow.entity.Menu;
import com.ykanji.reserveflow.entity.Reservation;
import com.ykanji.reserveflow.entity.Staff;
import com.ykanji.reserveflow.repository.CustomerRepository;
import com.ykanji.reserveflow.repository.MenuRepository;
import com.ykanji.reserveflow.repository.ReservationRepository;
import com.ykanji.reserveflow.repository.StaffRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CustomerRepository customerRepository;
    private final StaffRepository staffRepository;
    private final MenuRepository menuRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            CustomerRepository customerRepository,
            StaffRepository staffRepository,
            MenuRepository menuRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.customerRepository = customerRepository;
        this.staffRepository = staffRepository;
        this.menuRepository = menuRepository;
    }

    public List<ReservationListDto> findReservationList() {

        return reservationRepository.findAll()
                .stream()
                .map(reservation -> new ReservationListDto(
                        reservation.getId(),

                        reservation.getCustomer().getId(),
                        reservation.getCustomer().getName(),

                        reservation.getStaff().getId(),
                        reservation.getStaff().getName(),

                        reservation.getMenu().getId(),
                        reservation.getMenu().getName(),

                        reservation.getStartTime(),
                        reservation.getEndTime()
                ))
                .toList();
    }

    public Long createReservation(ReservationCreateRequest request) {

        if (request.getStartTime() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "startTime is required");
        }

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

        Staff staff = staffRepository.findById(request.getStaffId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff not found"));

        Menu menu = menuRepository.findById(request.getMenuId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Menu not found"));

        if (menu.getDurationMinutes() == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Menu has no duration set");
        }

        LocalDateTime startTime = request.getStartTime();
        LocalDateTime endTime = startTime.plusMinutes(menu.getDurationMinutes());

        if (reservationRepository.existsOverlapping(staff.getId(), startTime, endTime)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Staff is already booked for this time");
        }

        Reservation reservation = new Reservation(
                customer,
                staff,
                menu,
                request.getMemo(),
                startTime,
                endTime
        );

        return reservationRepository.save(reservation).getId();
    }
}
