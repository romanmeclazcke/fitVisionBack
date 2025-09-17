package org.example.fitvisionback.credits.service;

import org.example.fitvisionback.credits.model.Credits;
import org.example.fitvisionback.plan.entity.Plan;
import org.example.fitvisionback.user.entity.User;

public interface CreditsService {
    Boolean userHasCredits(User userConected);
    void useCredit(User userConected);
    void addCreditsToUser(User user, Plan plan);
    void save(Credits credits);
}
