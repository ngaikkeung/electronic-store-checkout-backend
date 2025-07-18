package io.github.kkngai.estorecheckout.service;

import io.github.kkngai.estorecheckout.model.User;
import io.github.kkngai.estorecheckout.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    public Optional<User> getUserById(Long id) {
        return userMapper.findById(id);
    }
}
