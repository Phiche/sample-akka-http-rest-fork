package com.example.migratedservice.repository;

import com.example.migratedservice.model.Hotel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test") // Ensure H2 profile is used
public class HotelRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private HotelRepository hotelRepository;

    @Test
    public void whenSaveHotel_thenFindById_returnsHotel() {
        // Arrange
        Hotel newHotel = new Hotel("Grand Hotel", "123 Main St", "10001");
        
        // Act
        Hotel savedHotel = hotelRepository.save(newHotel);
        Optional<Hotel> foundHotelOpt = hotelRepository.findById(savedHotel.getId());

        // Assert
        assertThat(foundHotelOpt).isPresent();
        Hotel foundHotel = foundHotelOpt.get();
        assertThat(foundHotel.getId()).isEqualTo(savedHotel.getId());
        assertThat(foundHotel.getName()).isEqualTo("Grand Hotel");
        assertThat(foundHotel.getAddress()).isEqualTo("123 Main St");
        assertThat(foundHotel.getZip()).isEqualTo("10001");
    }

    @Test
    public void whenFindByName_thenReturnMatchingHotels() {
        // Arrange
        Hotel hotel1 = new Hotel("Park Plaza", "1 Park Ave", "10002");
        Hotel hotel2 = new Hotel("Park Plaza", "2 Park Ave", "10003"); // Same name, different address
        Hotel hotel3 = new Hotel("Downtown Inn", "10 Market St", "10004");
        
        entityManager.persist(hotel1);
        entityManager.persist(hotel2);
        entityManager.persist(hotel3);
        entityManager.flush(); // Ensure data is persisted before query

        // Act
        List<Hotel> foundHotels = hotelRepository.findByName("Park Plaza");

        // Assert
        assertThat(foundHotels).hasSize(2);
        assertThat(foundHotels).extracting(Hotel::getName).containsOnly("Park Plaza");
        // Check if both specific hotels are present (order might not be guaranteed)
        assertThat(foundHotels).containsExactlyInAnyOrder(hotel1, hotel2);
    }

    @Test
    public void whenFindAll_thenReturnAllHotels() {
        // Arrange
        Hotel hotel1 = new Hotel("Sunset Lodge", "1 Sunset Blvd", "90210");
        Hotel hotel2 = new Hotel("Mountain View", "10 Mountain Rd", "80302");
        
        entityManager.persist(hotel1);
        entityManager.persist(hotel2);
        entityManager.flush();

        // Act
        List<Hotel> allHotels = hotelRepository.findAll();

        // Assert
        assertThat(allHotels).hasSize(2);
        assertThat(allHotels).containsExactlyInAnyOrder(hotel1, hotel2);
    }

    @Test
    public void whenUpdateHotel_thenChangesArePersisted() {
        // Arrange
        Hotel hotel = new Hotel("Old Name Inn", "Old Address", "00000");
        Hotel savedHotel = entityManager.persistFlushFind(hotel); // Persist and get managed instance
        
        // Act
        // Detach and modify to simulate updating a detached entity, or find and modify
        // For simplicity, we'll modify the managed entity directly
        savedHotel.setName("New Name Inn");
        hotelRepository.save(savedHotel); // This might be redundant if entity is managed, but good for clarity
        
        Optional<Hotel> updatedHotelOpt = hotelRepository.findById(savedHotel.getId());

        // Assert
        assertThat(updatedHotelOpt).isPresent();
        Hotel updatedHotel = updatedHotelOpt.get();
        assertThat(updatedHotel.getName()).isEqualTo("New Name Inn");
        assertThat(updatedHotel.getAddress()).isEqualTo("Old Address"); // Ensure other fields are unchanged
    }

    @Test
    public void whenDeleteHotel_thenItIsRemoved() {
        // Arrange
        Hotel hotel = new Hotel("To Be Deleted", "1 Delete Ln", "00000");
        Hotel savedHotel = entityManager.persistFlushFind(hotel);
        Long hotelId = savedHotel.getId();

        // Act
        hotelRepository.deleteById(hotelId);
        // entityManager.flush(); // Ensure delete operation is flushed to DB

        Optional<Hotel> deletedHotelOpt = hotelRepository.findById(hotelId);

        // Assert
        assertThat(deletedHotelOpt).isNotPresent();
    }
}
