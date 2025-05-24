package com.example.migratedservice.repository;

import com.example.migratedservice.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    /**
     * Finds hotels by their name.
     * Spring Data JPA will automatically generate the implementation for this method.
     * This corresponds to the original getHotelsByName method in HotelService.scala.
     *
     * @param name The name of the hotel to search for.
     * @return A list of hotels with the given name.
     */
    List<Hotel> findByName(String name);
}
