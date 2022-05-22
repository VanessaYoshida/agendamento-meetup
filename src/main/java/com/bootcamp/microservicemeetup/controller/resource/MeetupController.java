package com.bootcamp.microservicemeetup.controller.resource;

import com.bootcamp.microservicemeetup.controller.dto.EventMeetupDTO;
import com.bootcamp.microservicemeetup.controller.dto.MeetupDTO;
import com.bootcamp.microservicemeetup.controller.dto.MeetupUpdateDTO;
import com.bootcamp.microservicemeetup.controller.dto.RegistrationDTO;
import com.bootcamp.microservicemeetup.model.entity.Meetup;
import com.bootcamp.microservicemeetup.model.entity.Registration;
import com.bootcamp.microservicemeetup.service.MeetupService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/meetups")
@RequiredArgsConstructor
public class MeetupController {
    private final MeetupService meetupService;
    private final ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    private Integer createEvent(@RequestBody EventMeetupDTO eventMeetupDTO) {

        Meetup entity = Meetup.builder()
                .event(eventMeetupDTO.getEvent())
                .meetupDate(eventMeetupDTO.getDate().toString())
                .ownerId(eventMeetupDTO.getOwnerId())
                .build();

        entity = meetupService.save(entity);
        return entity.getId();
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public MeetupDTO get(@PathVariable Integer id) {

        return meetupService
                .getById(id)
                .map(meetup -> modelMapper.map(meetup, MeetupDTO.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public Page<MeetupDTO> find(MeetupDTO dto, Pageable pageRequest) {
        Meetup meetup = modelMapper.map(dto, Meetup.class);
        Page<Meetup> result = meetupService.getRegistrationsByMeetup(meetup, pageRequest);
        List<MeetupDTO> meetups = result
                .getContent()
                .stream()
                .map(entity -> {

                    List<Registration> registrations = entity.getRegistrations();

                    List<RegistrationDTO> registrationDTOS = registrations.stream()
                            .map(registration -> modelMapper.map(registration, RegistrationDTO.class))
                            .collect(Collectors.toList());

                    MeetupDTO meetupDTO = modelMapper.map(entity, MeetupDTO.class);
                    meetupDTO.setRegistrations(registrationDTOS);

                    return meetupDTO;

                }).collect(Collectors.toList());
        return new PageImpl<MeetupDTO>(meetups, pageRequest, result.getTotalElements());
    }

    @PutMapping("{id}")
    public MeetupUpdateDTO update(@PathVariable Integer id, @RequestBody MeetupUpdateDTO meetupUpdateDTO) {

        return meetupService.getById(id).map(meetup -> {
            meetup.setEvent(meetupUpdateDTO.getEvent());
            meetup.setMeetupDate(meetupUpdateDTO.getDate());
            meetup.setOwnerId(meetupUpdateDTO.getOwnerId());
            meetup = meetupService.update(meetup);

            return modelMapper.map(meetup, MeetupUpdateDTO.class);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> deleteByMeetupEvent(@PathVariable Integer id) {

        Meetup meetupId = meetupService.getById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND));

        meetupService.delete(meetupId);

        return ResponseEntity.ok().build();
    }
}
