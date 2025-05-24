package com.example.migratedservice.controller;

import com.example.migratedservice.model.Hotel;
import com.example.migratedservice.service.HotelService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;


@WebMvcTest(HotelController.class)
@ActiveProfiles("test") // Ensure a test profile is active, if any specific test configurations are needed
public class HotelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HotelService hotelService;

    @Autowired
    private ObjectMapper objectMapper; // For converting objects to JSON strings

    private Hotel hotel1;
    private Hotel hotel2;
    private Hotel hotelInput;

    @BeforeEach
    void setUp() {
        hotel1 = new Hotel(1L, "Hotel California", "123 Sunset Blvd", "90210");
        hotel2 = new Hotel(2L, "Grand Budapest Hotel", "Alpine Rd", "1138");
        hotelInput = new Hotel("New Hotel", "Address", "Zip"); // Used for POST/PUT requests
    }

    @Test
    void testGetAllHotels_shouldReturnListOfHotels() throws Exception {
        List<Hotel> allHotels = Arrays.asList(hotel1, hotel2);
        when(hotelService.getHotels()).thenReturn(allHotels);

        mockMvc.perform(get("/api/v1/hotels"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is(hotel1.getName())))
                .andExpect(jsonPath("$[1].name", is(hotel2.getName())));
    }

    @Test
    void testCreateHotel_shouldReturnCreatedHotel() throws Exception {
        when(hotelService.createHotel(any(Hotel.class))).thenReturn(hotel1); // Assume hotel1 is the created one with ID

        mockMvc.perform(post("/api/v1/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hotelInput)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(hotel1.getId().intValue())))
                .andExpect(jsonPath("$.name", is(hotel1.getName())));
    }

    @Test
    void testGetHotelById_whenHotelExists_shouldReturnHotel() throws Exception {
        when(hotelService.getHotel(1L)).thenReturn(Optional.of(hotel1));

        mockMvc.perform(get("/api/v1/hotels/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(hotel1.getId().intValue())))
                .andExpect(jsonPath("$.name", is(hotel1.getName())));
    }

    @Test
    void testGetHotelById_whenHotelNotExists_shouldReturnNotFound() throws Exception {
        when(hotelService.getHotel(3L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/hotels/3"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateHotel_whenHotelExists_shouldReturnUpdatedHotel() throws Exception {
        Hotel updatedHotel = new Hotel(1L, "Updated Hotel Name", "Updated Address", "UpdatedZip");
        when(hotelService.updateHotel(eq(1L), any(Hotel.class))).thenReturn(Optional.of(updatedHotel));

        mockMvc.perform(put("/api/v1/hotels/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new Hotel("Updated Hotel Name", "Updated Address", "UpdatedZip"))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Updated Hotel Name")))
                .andExpect(jsonPath("$.address", is("Updated Address")));
    }

    @Test
    void testUpdateHotel_whenHotelNotExists_shouldReturnNotFound() throws Exception {
        when(hotelService.updateHotel(eq(3L), any(Hotel.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/v1/hotels/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hotelInput)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteHotel_shouldReturnNoContent() throws Exception {
        doNothing().when(hotelService).deleteHotel(1L);

        mockMvc.perform(delete("/api/v1/hotels/1"))
                .andExpect(status().isNoContent());
    }
}
