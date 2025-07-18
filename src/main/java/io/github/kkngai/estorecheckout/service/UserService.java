package io.github.kkngai.estorecheckout.service;

import io.github.kkngai.estorecheckout.model.User;
import io.github.kkngai.estorecheckout.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
}
