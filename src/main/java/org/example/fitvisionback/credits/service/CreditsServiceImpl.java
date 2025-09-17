package org.example.fitvisionback.credits.service;

import lombok.extern.slf4j.Slf4j;
import org.example.fitvisionback.credits.model.Credits;
import org.example.fitvisionback.credits.repository.CreditsRepository;
import org.example.fitvisionback.plan.entity.Plan;
import org.example.fitvisionback.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class CreditsServiceImpl implements CreditsService{


    private CreditsRepository creditsRepository;

    @Autowired
    public CreditsServiceImpl(CreditsRepository creditsRepository) {
        this.creditsRepository = creditsRepository;
    }

    @Override
    public Boolean userHasCredits(User userConected) {
        Credits credits= this.creditsRepository.findByUser(userConected);

        return Objects.nonNull(credits) && credits.getCredits() > 0;
    }

    @Override
    public void useCredit(User userConected) {
        Credits credits = this.creditsRepository.findByUser(userConected);
        credits.setCredits(credits.getCredits()-1);
        this.creditsRepository.save(credits);
    }

    @Override
    public void addCreditsToUser(User user, Plan plan) {
        Credits creditsOffUser = this.creditsRepository.findByUser(user);
        creditsOffUser.setCredits(plan.getCredits());
        this.creditsRepository.save(creditsOffUser);

    }

    @Override
    public void save(Credits credits) {
        this.creditsRepository.save(credits);
    }
}
