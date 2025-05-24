package com.example.migratedservice.service;

import com.example.migratedservice.model.Hotel;
import com.example.migratedservice.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HotelServiceTest {

    @Mock
    private HotelRepository hotelRepository;

    @InjectMocks
    private HotelService hotelService;

    private Hotel hotel1;
    private Hotel hotel2;

    @BeforeEach
    void setUp() {
        hotel1 = new Hotel(1L, "Hotel California", "123 Sunset Blvd", "90210");
        hotel2 = new Hotel(2L, "Grand Budapest Hotel", "Alpine Rd", "1138");
    }

    @Test
    void testGetHotels_shouldReturnListOfHotels() {
        // Arrange
        List<Hotel> hotels = Arrays.asList(hotel1, hotel2);
        when(hotelRepository.findAll()).thenReturn(hotels);

        // Act
        List<Hotel> result = hotelService.getHotels();

        // Assert
        assertThat(result).isEqualTo(hotels);
        verify(hotelRepository).findAll();
    }

    @Test
    void testGetHotelById_whenHotelExists_shouldReturnHotel() {
        // Arrange
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel1));

        // Act
        Optional<Hotel> result = hotelService.getHotel(1L);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(hotel1);
        verify(hotelRepository).findById(1L);
    }

    @Test
    void testGetHotelById_whenHotelNotExists_shouldReturnEmptyOptional() {
        // Arrange
        when(hotelRepository.findById(3L)).thenReturn(Optional.empty());

        // Act
        Optional<Hotel> result = hotelService.getHotel(3L);

        // Assert
        assertThat(result).isNotPresent();
        verify(hotelRepository).findById(3L);
    }

    @Test
    void testGetHotelsByName_shouldReturnMatchingHotels() {
        // Arrange
        List<Hotel> californiaHotels = Arrays.asList(hotel1);
        when(hotelRepository.findByName("Hotel California")).thenReturn(californiaHotels);

        // Act
        List<Hotel> result = hotelService.getHotelsByName("Hotel California");

        // Assert
        assertThat(result).isEqualTo(californiaHotels);
        verify(hotelRepository).findByName("Hotel California");
    }

    @Test
    void testCreateHotel_shouldSaveAndReturnHotel() {
        // Arrange
        Hotel newHotel = new Hotel("New Resort", "Beach Front", "12345"); // No ID
        Hotel savedHotel = new Hotel(3L, "New Resort", "Beach Front", "12345"); // With ID

        // Mock repository.save to assign an ID and return the hotel
        when(hotelRepository.save(any(Hotel.class))).thenAnswer(invocation -> {
            Hotel hotelToSave = invocation.getArgument(0);
            // Ensure the ID is null before saving (as per service logic)
            assertThat(hotelToSave.getId()).isNull();
            return new Hotel(3L, hotelToSave.getName(), hotelToSave.getAddress(), hotelToSave.getZip());
        });
        
        ArgumentCaptor<Hotel> hotelCaptor = ArgumentCaptor.forClass(Hotel.class);

        // Act
        Hotel result = hotelService.createHotel(newHotel);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getName()).isEqualTo("New Resort");
        
        verify(hotelRepository).save(hotelCaptor.capture());
        Hotel capturedHotel = hotelCaptor.getValue();
        assertThat(capturedHotel.getId()).isNull(); // Service sets ID to null before calling save
        assertThat(capturedHotel.getName()).isEqualTo("New Resort");
    }


    @Test
    void testUpdateHotel_whenHotelExists_shouldUpdateAndReturnHotel() {
        // Arrange
        Hotel existingHotel = new Hotel(1L, "Old Name", "Old Address", "00000");
        Hotel hotelDetailsToUpdate = new Hotel("New Name", "New Address", "11111");
        
        Hotel updatedHotelFromRepo = new Hotel(1L, "New Name", "New Address", "11111");

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(existingHotel));
        when(hotelRepository.save(any(Hotel.class))).thenReturn(updatedHotelFromRepo);
        
        ArgumentCaptor<Hotel> hotelCaptor = ArgumentCaptor.forClass(Hotel.class);

        // Act
        Optional<Hotel> result = hotelService.updateHotel(1L, hotelDetailsToUpdate);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("New Name");
        assertThat(result.get().getAddress()).isEqualTo("New Address");
        assertThat(result.get().getZip()).isEqualTo("11111");
        assertThat(result.get().getId()).isEqualTo(1L); // ID should not change

        verify(hotelRepository).findById(1L);
        verify(hotelRepository).save(hotelCaptor.capture());
        
        Hotel savedHotel = hotelCaptor.getValue();
        assertThat(savedHotel.getId()).isEqualTo(1L); // Ensure ID is preserved for update
        assertThat(savedHotel.getName()).isEqualTo("New Name");
    }

    @Test
    void testUpdateHotel_whenHotelNotExists_shouldReturnEmptyOptional() {
        // Arrange
        Hotel hotelDetailsToUpdate = new Hotel("New Name", "New Address", "11111");
        when(hotelRepository.findById(3L)).thenReturn(Optional.empty());

        // Act
        Optional<Hotel> result = hotelService.updateHotel(3L, hotelDetailsToUpdate);

        // Assert
        assertThat(result).isNotPresent();
        verify(hotelRepository).findById(3L);
        verify(hotelRepository, never()).save(any(Hotel.class));
    }

    @Test
    void testDeleteHotel_shouldCallRepositoryDelete() {
        // Arrange
        Long hotelIdToDelete = 1L;
        // Mocking void method
        doNothing().when(hotelRepository).deleteById(hotelIdToDelete);

        // Act
        hotelService.deleteHotel(hotelIdToDelete);

        // Assert
        verify(hotelRepository).deleteById(hotelIdToDelete);
    }
}
