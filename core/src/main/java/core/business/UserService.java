package core.business;

import integration.api.repository.Repository;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import core.model.User;

/**
 * Handles all operations related to users.
 */
@Service
public class UserService {

    Logger logger = LoggerFactory.getLogger(MessageService.class);

    private final Repository<User> repository;

    @Inject
    public UserService(Repository<User> repository) {
        this.repository = repository;
    }

    /**
     * Counts the number of users in the repository.
     * 
     * @return The total number of users in the repository.
     */
    public long count() {
        return repository.count();
    }
}
