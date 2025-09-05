package com.jobHunter.demoAPI.service.impl;

import com.jobHunter.demoAPI.domain.dto.email.RestEmailJobDTO;
import com.jobHunter.demoAPI.domain.dto.pagination.ResultPaginationDTO;
import com.jobHunter.demoAPI.domain.dto.subscriber.RestSubscriberCreateDTO;
import com.jobHunter.demoAPI.domain.dto.subscriber.RestSubscriberUpdateDTO;
import com.jobHunter.demoAPI.domain.dto.subscriber.RestSubscriberViewDTO;
import com.jobHunter.demoAPI.domain.entity.Job;
import com.jobHunter.demoAPI.domain.entity.Skill;
import com.jobHunter.demoAPI.domain.entity.Subscriber;
import com.jobHunter.demoAPI.repository.JobRepository;
import com.jobHunter.demoAPI.repository.SkillRepository;
import com.jobHunter.demoAPI.repository.SubscriberRepository;
import com.jobHunter.demoAPI.service.EmailService;
import com.jobHunter.demoAPI.service.SubscriberService;
import com.jobHunter.demoAPI.util.pagination.PageUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class SubscriberServiceImpl implements SubscriberService {

    private final SubscriberRepository subscriberRepository;

    private final SkillRepository skillRepository;

    private final JobRepository jobRepository;

    private final EmailService emailService;

    public SubscriberServiceImpl(
            SubscriberRepository subscriberRepository,
            SkillRepository skillRepository,
            JobRepository jobRepository,
            EmailService emailService
    ) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
        this.emailService = emailService;
    }

    private void checkExistAndSetSkills(Subscriber subscriberRequest, Subscriber currentSubscriber) {
        if (subscriberRequest.getSkills() != null
                && !subscriberRequest.getSkills().isEmpty()
        ) {
            for (Skill skill : subscriberRequest.getSkills()) {
                if (!this.skillRepository.existsById(skill.getId())) {
                    throw new NoSuchElementException("Skill with id " + skill.getId() + " does not exist");
                }
            }

            List<Long> skillIds = subscriberRequest.getSkills().stream()
                    .map(Skill::getId)
                    .toList();

            List<Skill> skills = this.skillRepository.findAllByIdIn(skillIds);
            currentSubscriber.setSkills(skills);
        }
    }

    @Transactional
    @Override
    public Subscriber createSubscriber(Subscriber subscriber) {
        if (this.checkEmailExists(subscriber.getEmail())) {
            throw new IllegalArgumentException(String.format("Email '%s' already exists", subscriber.getEmail()));
        }
        this.checkExistAndSetSkills(subscriber, subscriber);
        return this.subscriberRepository.save(subscriber);
    }

    @Transactional
    @Override
    public Subscriber updateSubscriberById(Subscriber subscriberUpdated, Long id) {
        Subscriber subscriberGetById = this.getSubscriberById(id);
        this.checkExistAndSetSkills(subscriberUpdated, subscriberGetById);
        return this.subscriberRepository.save(subscriberGetById);
    }

    @Override
    public Subscriber getSubscriberById(Long id) {
        return this.subscriberRepository
                .findById(id)
                .orElseThrow(() -> new NoSuchElementException("Subscriber with id " + id + " does not exist!"));
    }

    @Transactional
    @Override
    public void deleteSubscriberById(Long id) {
        if (!this.checkIdExists(id)) {
            throw new NoSuchElementException("Subscriber with id " + id + " does not exist!");
        }
        this.subscriberRepository.deleteById(id);
    }

    @Override
    public ResultPaginationDTO fetchAllSubscribers(Specification<Subscriber> spec, Pageable pageable) {
        Page<Subscriber> pageHavSubscribers = this.subscriberRepository.findAll(spec, pageable);

        List<RestSubscriberViewDTO> restSubscriberViewDTOList = pageHavSubscribers.getContent()
                .stream()
                .map(this::convertSubscriberToRestSubscriberViewDTO)
                .toList();

        ResultPaginationDTO resultPaginationDTO = PageUtil.handleFetchAllDataWithPagination(pageHavSubscribers, pageable);
        resultPaginationDTO.setResult(restSubscriberViewDTOList);

        return resultPaginationDTO;
    }

    @Override
    public RestSubscriberViewDTO convertSubscriberToRestSubscriberViewDTO(Subscriber subscriber) {
        List<String> skillsList = Optional.ofNullable(subscriber.getSkills())
                .map(skills -> skills.stream()
                        .map(Skill::getName)
                        .toList()
                )
                .orElse(null);

        return new RestSubscriberViewDTO(
                subscriber.getId(),
                subscriber.getName(),
                subscriber.getEmail(),
                subscriber.getCreatedAt(),
                subscriber.getUpdatedAt(),
                subscriber.getCreatedBy(),
                subscriber.getUpdatedBy(),
                skillsList
        );
    }

    @Override
    public RestSubscriberCreateDTO convertSubscriberToRestSubscriberCreateDTO(Subscriber subscriber) {
        List<String> skillsList = Optional.ofNullable(subscriber.getSkills())
                .map(skills -> skills.stream()
                        .map(Skill::getName)
                        .toList()
                )
                .orElse(null);

        return new RestSubscriberCreateDTO(
                subscriber.getId(),
                subscriber.getName(),
                subscriber.getEmail(),
                subscriber.getCreatedAt(),
                subscriber.getCreatedBy(),
                skillsList
        );
    }

    @Override
    public RestSubscriberUpdateDTO convertSubscriberToRestSubscriberUpdateDTO(Subscriber subscriber) {
        List<String> skillsList = Optional.ofNullable(subscriber.getSkills())
                .map(skills -> skills.stream()
                        .map(Skill::getName)
                        .toList()
                )
                .orElse(null);

        return new RestSubscriberUpdateDTO(
                subscriber.getId(),
                subscriber.getName(),
                subscriber.getEmail(),
                subscriber.getUpdatedAt(),
                subscriber.getUpdatedBy(),
                skillsList
        );
    }

    @Override
    public boolean checkIdExists(Long id) {
        return this.subscriberRepository.existsById(id);
    }

    @Override
    public boolean checkEmailExists(String email) {
        return this.subscriberRepository.existsByEmail(email);
    }

    @Override
    public Subscriber getSubscriberByEmail(String email) {
        return this.subscriberRepository.findByEmail(email);
    }

    @Async
    @Transactional
    @Override
    public void sendSubscribersEmailJobs() {
        List<Subscriber> subscriberList = this.subscriberRepository.findAll();
        for (Subscriber subscriber : subscriberList) {
            List<Skill> skillList = subscriber.getSkills();

            if (skillList != null && !skillList.isEmpty()) {
                List<Job> jobList = this.jobRepository.findBySkillsIn(skillList);

                if (jobList != null && !jobList.isEmpty()) {
                    List<RestEmailJobDTO> restEmailJobDTOList = jobList.stream()
                            .map(this::convertSubscriberToRestEmailJobDTO)
                            .toList();

                    this.emailService.sendEmailWithTemplateSync(
                            subscriber.getEmail(),
                            "Cơ hội việc làm đang đón chờ bạn",
                            "job",
                            subscriber.getName(),
                            restEmailJobDTOList
                    );
                }
            }
        }
    }

    private RestEmailJobDTO convertSubscriberToRestEmailJobDTO(Job job) {
        RestEmailJobDTO.CompanyEmailJob companyEmailJob = Optional.ofNullable(job.getCompany())
                .map(company -> new RestEmailJobDTO.CompanyEmailJob(company.getName()))
                .orElse(null);

        List<RestEmailJobDTO.SkillEmailJob> skillEmailJobList = Optional.ofNullable(job.getSkills())
                .map(skills -> skills.stream()
                        .map(skill -> new RestEmailJobDTO.SkillEmailJob(skill.getName()))
                        .toList()
                )
                .orElse(null);

        return new RestEmailJobDTO(
                job.getName(),
                job.getSalary(),
                companyEmailJob,
                skillEmailJobList
        );
    }

//    @Scheduled(cron = "*/10 * * * * *")
//    private void testCron() {
//        System.out.println(" >>> TEST CRON!");
//    }
}
