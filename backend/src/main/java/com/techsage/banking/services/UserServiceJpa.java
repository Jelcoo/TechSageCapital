package com.techsage.banking.services;

import com.techsage.banking.models.User;
import com.techsage.banking.models.dto.UserDto;
import com.techsage.banking.repositories.UserRepository;
import com.techsage.banking.services.interfaces.UserService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceJpa implements UserService {
    private final UserRepository userRepository;
    ModelMapper modelMapper;

    public UserServiceJpa(UserRepository userRepository) {
        this.userRepository = userRepository;
        modelMapper = new ModelMapper();
    }

    @Override
    public List<UserDto> getAll() {
        List<User> users = (List<User>)userRepository.findAll();
        return users.stream().map(user -> modelMapper.map(user, UserDto.class)).toList();
    }

    @Override
    public User getById(int id) {
        return userRepository.findById(id).get();
    }

    @Override
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(User user) {
        return userRepository.save(user);
    }

    @Override
    public void delete(int id) {
        userRepository.deleteById(id);
    }
}
