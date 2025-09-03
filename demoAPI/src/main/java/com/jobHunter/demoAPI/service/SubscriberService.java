package com.jobHunter.demoAPI.service;

import com.jobHunter.demoAPI.domain.dto.pagination.ResultPaginationDTO;
import com.jobHunter.demoAPI.domain.dto.subscriber.RestSubscriberCreateDTO;
import com.jobHunter.demoAPI.domain.dto.subscriber.RestSubscriberUpdateDTO;
import com.jobHunter.demoAPI.domain.dto.subscriber.RestSubscriberViewDTO;
import com.jobHunter.demoAPI.domain.entity.Subscriber;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface SubscriberService {
    Subscriber createSubscriber(Subscriber subscriber);
    Subscriber updateSubscriberById(Subscriber subscriber, Long id);
    Subscriber getSubscriberById(Long id);
    Subscriber getSubscriberByEmail(String email);

    ResultPaginationDTO fetchAllSubscribers(Specification<Subscriber> spec, Pageable pageable);

    RestSubscriberViewDTO convertSubscriberToRestSubscriberViewDTO(Subscriber subscriber);
    RestSubscriberCreateDTO  convertSubscriberToRestSubscriberCreateDTO(Subscriber subscriber);
    RestSubscriberUpdateDTO convertSubscriberToRestSubscriberUpdateDTO(Subscriber subscriber);

    boolean checkIdExists(Long id);
    boolean checkEmailExists(String email);

    void sendSubscribersEmailJobs();
    void deleteSubscriberById(Long id);
}
