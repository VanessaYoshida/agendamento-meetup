package com.bootcamp.microservicemeetup.service;

import com.bootcamp.microservicemeetup.model.entity.Meetup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface MeetupService {

  Meetup save(Meetup meetup);

  Optional<Meetup> getById(Integer id);

  Page<Meetup> findAll(PageRequest pageRequest);

  Meetup update(Meetup loan);

  void delete(Meetup meetup);

  Page<Meetup> getRegistrationsByMeetup(Meetup meetup, Pageable pageable);
}
