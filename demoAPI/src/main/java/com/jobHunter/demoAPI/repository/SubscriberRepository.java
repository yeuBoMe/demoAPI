package com.jobHunter.demoAPI.repository;

import com.jobHunter.demoAPI.domain.entity.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriberRepository extends JpaRepository<Subscriber, Long>, JpaSpecificationExecutor<Subscriber> {

    // check exists
    boolean existsByEmail(String email);

    // find by email
    Subscriber findByEmail(String email);
}
