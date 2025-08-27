package com.jobHunter.demoAPI.controller;

import com.jobHunter.demoAPI.domain.dto.pagination.ResultPaginationDTO;
import com.jobHunter.demoAPI.domain.dto.subscriber.RestSubscriberCreateDTO;
import com.jobHunter.demoAPI.domain.dto.subscriber.RestSubscriberUpdateDTO;
import com.jobHunter.demoAPI.domain.dto.subscriber.RestSubscriberViewDTO;
import com.jobHunter.demoAPI.domain.entity.Subscriber;
import com.jobHunter.demoAPI.service.SubscriberService;
import com.jobHunter.demoAPI.util.annotation.ApiMessage;
import com.jobHunter.demoAPI.util.security.SecurityUtil;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/subscribers")
public class SubscriberController {

    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping
    public ResponseEntity<RestSubscriberCreateDTO> createSubscriberRequest(@Valid @RequestBody Subscriber subscriber) {
        Subscriber subscriberCreated = this.subscriberService.createSubscriber(subscriber);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.subscriberService.convertSubscriberToRestSubscriberCreateDTO(subscriberCreated));
    }

    @GetMapping
    public ResponseEntity<ResultPaginationDTO> fetchAllSubscribersRequest(
            @Filter Specification<Subscriber> spec,
            Pageable pageable
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.subscriberService.fetchAllSubscribers(spec, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestSubscriberViewDTO> getSubscriberByIdRequest(@PathVariable Long id) {
        Subscriber subscriberGetById = this.subscriberService.getSubscriberById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.subscriberService.convertSubscriberToRestSubscriberViewDTO(subscriberGetById));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestSubscriberUpdateDTO> updateSubscriberByIdRequest(
            @PathVariable Long id,
            @Valid @RequestBody Subscriber subscriber
    ) {
        Subscriber subscriberUpdated = this.subscriberService.updateSubscriberById(subscriber, id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.subscriberService.convertSubscriberToRestSubscriberUpdateDTO(subscriberUpdated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubscriberByIdRequest(@PathVariable Long id) {
        this.subscriberService.deleteSubscriberById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(null);
    }

    @PostMapping("/skills")
    @ApiMessage("Get subscriber's skills")
    public ResponseEntity<Subscriber> getSkillsOfUserSubscribeRequest() {
        String email = SecurityUtil.getCurrentUserLogin().orElseThrow(
                () -> new NoSuchElementException("Subscriber not found")
        );
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.subscriberService.getSubscriberByEmail(email));
    }
}
