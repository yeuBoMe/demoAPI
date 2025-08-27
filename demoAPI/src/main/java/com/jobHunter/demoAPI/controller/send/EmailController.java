package com.jobHunter.demoAPI.controller.send;

import com.jobHunter.demoAPI.service.SubscriberService;
import com.jobHunter.demoAPI.util.annotation.ApiMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/email")
public class EmailController {

    private final SubscriberService subscriberService;

    public EmailController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @GetMapping
    @ApiMessage("Send email")
    public ResponseEntity<Void> sendSimpleEmailRequest() {
        this.subscriberService.sendSubscribersEmailJobs();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(null);
    }
}
