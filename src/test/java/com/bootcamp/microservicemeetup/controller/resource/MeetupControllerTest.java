package com.bootcamp.microservicemeetup.controller.resource;

import com.bootcamp.microservicemeetup.controller.dto.EventMeetupDTO;
import com.bootcamp.microservicemeetup.controller.dto.MeetupUpdateDTO;
import com.bootcamp.microservicemeetup.exception.BusinessException;
import com.bootcamp.microservicemeetup.model.entity.Meetup;
import com.bootcamp.microservicemeetup.model.entity.Registration;
import com.bootcamp.microservicemeetup.service.MeetupService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = {MeetupController.class})
@AutoConfigureMockMvc
public class MeetupControllerTest {

    static final String MEETUP_API = "/api/meetups";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    MeetupService meetupService;

    @Test
    @DisplayName("Should register on a meetup")
    public void createEventMeetupTest() throws Exception {

        EventMeetupDTO dto = EventMeetupDTO.builder()
                .event("Womakerscode Dados")
                .date("20/06/2022")
                .ownerId(1)
                .build();
        String json = new ObjectMapper().writeValueAsString(dto);

        Meetup meetup = Meetup.builder()
                .id(11)
                .event("Womakerscode Dados")
                .meetupDate("20/06/2022")
                .ownerId(1)
                .build();

        BDDMockito.given(meetupService.save(Mockito.any(Meetup.class))).willReturn(meetup);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(MEETUP_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().string("11"));
    }

    @Test
    @DisplayName("Should return error when try to register a registration already register on a meetup")
    public void meetupRegistrationErrorOnCreateMeetupTest() throws Exception {

        EventMeetupDTO dto = EventMeetupDTO.builder()
                .event("Womakerscode Dados")
                .date("20/06/2022")
                .ownerId(1)
                .build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(meetupService.save(Mockito.any(Meetup.class)))
                .willThrow(new BusinessException("Meetup already enrolled"));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(MEETUP_API)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should get meetup registration")
    public void getMeetupTest() throws Exception {

        Integer idMeetup = 44;

        Meetup meetup = Meetup.builder()
                .id(idMeetup)
                .event("Womakerscode Dados")
                .meetupDate("20/06/2022")
                .registered(true)
                .build();

        String json = new ObjectMapper().writeValueAsString(meetup);

        BDDMockito.given(meetupService.getById(idMeetup)).willReturn(Optional.of(meetup));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(MEETUP_API.concat("/" + idMeetup))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("event").value("Womakerscode Dados"))
                .andExpect(jsonPath("date").value("20/06/2022"));
    }

    @Test
    @DisplayName("Should return empty when get an meetup registration by id when doesn't exists")
    public void meetupRegistrationNotFound() throws Exception {

        Integer idMeetup = 44;

        Meetup meetup = Meetup.builder()
                .id(idMeetup)
                .event("Womakerscode Dados")
                .meetupDate("20/06/2022")
                .registered(true)
                .build();

        String json = new ObjectMapper().writeValueAsString(meetup);

        BDDMockito.given(meetupService.getById(idMeetup)).willReturn(Optional.of(meetup));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(MEETUP_API.concat("/" + 45))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should delete the meetup")
    public void deleteMeetup() throws Exception {

        Integer idMeetup = 44;

        Meetup meetup = Meetup.builder()
                .id(idMeetup)
                .event("Womakerscode Dados")
                .meetupDate("20/06/2022")
                .registered(true)
                .build();
        String json = new ObjectMapper().writeValueAsString(meetup);

        BDDMockito.given(meetupService.getById(idMeetup)).willReturn(Optional.of(meetup));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(MEETUP_API.concat("/" + idMeetup))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return resource not found when no meetup is found to delete")
    public void tryDeleteMeetupThatDontExists() throws Exception {

        Integer idMeetup = 44;

        Meetup meetup = Meetup.builder()
                .id(idMeetup)
                .event("Womakerscode Dados")
                .meetupDate("20/06/2022")
                .registered(true)
                .build();
        String json = new ObjectMapper().writeValueAsString(meetup);

        meetupService.delete(meetup);
        BDDMockito.given(meetupService.getById(idMeetup)).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete(MEETUP_API.concat("/" + idMeetup))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should update when meetup info")
    public void updateMeetupTest() throws Exception {
        String json = new ObjectMapper().writeValueAsString(createMeetupUpdateDTO());

        Integer idMeetup = 44;

        Meetup meetup = Meetup.builder()
                .id(idMeetup)
                .event("Womakerscode Dados")
                .meetupDate("20/06/2022")
                .registered(true)
                .build();

        BDDMockito.given(meetupService.getById(idMeetup)).willReturn(Optional.of(meetup));

        meetup.setEvent("Womakercode Java");

        BDDMockito.given(meetupService.update(meetup)).willReturn(meetup);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(MEETUP_API.concat("/" + idMeetup))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should return meetup")
    void testFindAndShouldMeetup() throws Exception {

        Meetup meetup = Meetup.builder().event("WomakersCode").id(1).meetupDate("05-05-2022").ownerId(1)
                .registrations(List.of(Registration.builder().id(1)
                        .personId("1").dateOfRegistration(LocalDate.now()).name("VanessaYoshida").build())).build();

        BDDMockito.given(meetupService.getRegistrationsByMeetup(Mockito.any(Meetup.class),
                        Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Meetup>(Arrays.asList(meetup), PageRequest.of(0, 10), 1));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(MEETUP_API.concat("?event=WoMakersCode&page=0&size=10"))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(10))
                .andExpect(jsonPath("pageable.pageNumber").value(0));
    }

    @Test
    @DisplayName("Should return empty list")
    void testFindAndShouldReturnEmptyList() throws Exception {

        BDDMockito.given(meetupService.getRegistrationsByMeetup(Mockito.any(Meetup.class),
                        Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Meetup>(Collections.emptyList(), PageRequest.of(0, 10), 0));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(MEETUP_API.concat("?event=WoMakersCode&page=0&size=10"))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.hasSize(0)))
                .andExpect(jsonPath("totalElements").value(0))
                .andExpect(jsonPath("pageable.pageSize").value(10))
                .andExpect(jsonPath("pageable.pageNumber").value(0));
    }

    private MeetupUpdateDTO createMeetupUpdateDTO() {
        return MeetupUpdateDTO.builder().event("Womalercode").date("06-05-2022").ownerId(1)
                .build();
    }
}