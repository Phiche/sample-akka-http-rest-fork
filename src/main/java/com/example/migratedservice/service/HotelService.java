package com.example.migratedservice.service;

import com.example.migratedservice.model.Hotel;
import com.example.migratedservice.repository.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HotelService {

    private final HotelRepository hotelRepository;

    @Autowired
    public HotelService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @Transactional(readOnly = true)
    public List<Hotel> getHotels() {
        return hotelRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Hotel> getHotel(Long id) {
        return hotelRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Hotel> getHotelsByName(String name) {
        return hotelRepository.findByName(name);
    }

    public Hotel createHotel(Hotel hotel) {
        // Ensure ID is null to trigger an insert, not an update
        hotel.setId(null);
        return hotelRepository.save(hotel);
    }

    public Optional<Hotel> updateHotel(Long id, Hotel hotelDetails) {
        Optional<Hotel> optionalHotel = hotelRepository.findById(id);
        if (optionalHotel.isPresent()) {
            Hotel existingHotel = optionalHotel.get();
            existingHotel.setName(hotelDetails.getName());
            existingHotel.setAddress(hotelDetails.getAddress());
            existingHotel.setZip(hotelDetails.getZip());
            // Do not change the ID: existingHotel.setId(hotelDetails.getId());
            return Optional.of(hotelRepository.save(existingHotel));
        } else {
            return Optional.empty();
        }
    }

    public void deleteHotel(Long id) {
        // Spring Data JPA's deleteById doesn't throw an error if the entity doesn't exist.
        // If specific handling for non-existent ID is needed, check existence first:
        // if (hotelRepository.existsById(id)) {
        //     hotelRepository.deleteById(id);
        // } else {
        //     // throw new EntityNotFoundException("Hotel with id " + id + " not found");
        // }
        hotelRepository.deleteById(id);
    }
}
