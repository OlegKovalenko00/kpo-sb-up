package com.example.booking.services.users;

import com.example.booking.domains.User;
import com.example.booking.dto.request.UserRequest;
import com.example.booking.exception.BookingException;
import com.example.booking.repositories.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public User create(UserRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(u -> {
            throw new BookingException("User with email already exists");
        });
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .active(request.isActive())
                .build();
        return userRepository.save(user);
    }

    @Transactional
    public User update(Long id, UserRequest request) {
        User user = getById(id);
        userRepository.findByEmail(request.getEmail()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new BookingException("User with email already exists");
            }
        });
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setActive(request.isActive());
        return userRepository.save(user);
    }

    @Transactional
    public void delete(Long id) {
        User user = getById(id);
        userRepository.delete(user);
    }

    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new BookingException("User not found"));
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }
}
