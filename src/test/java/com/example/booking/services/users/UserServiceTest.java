package com.example.booking.services.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.booking.domains.User;
import com.example.booking.dto.request.UserRequest;
import com.example.booking.exception.BookingException;
import com.example.booking.repositories.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateUser() {
        UserRequest request = new UserRequest();
        request.setName("Alice");
        request.setEmail("alice@example.com");
        request.setActive(true);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        User result = userService.create(request);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo(request.getName());
        assertThat(result.getEmail()).isEqualTo(request.getEmail());
        assertThat(result.isActive()).isTrue();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowWhenEmailExistsOnCreate() {
        UserRequest request = new UserRequest();
        request.setName("Alice");
        request.setEmail("alice@example.com");
        request.setActive(true);

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(User.builder().id(2L).build()));

        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(BookingException.class)
                .hasMessageContaining("User with email already exists");
    }

    @Test
    void shouldUpdateUser() {
        User existing = User.builder().id(1L).name("Old").email("old@example.com").active(false).build();
        UserRequest request = new UserRequest();
        request.setName("New");
        request.setEmail("new@example.com");
        request.setActive(true);

        when(userRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existing));
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        User updated = userService.update(existing.getId(), request);

        assertThat(updated.getName()).isEqualTo(request.getName());
        assertThat(updated.getEmail()).isEqualTo(request.getEmail());
        assertThat(updated.isActive()).isTrue();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowWhenEmailTakenByAnotherOnUpdate() {
        User existing = User.builder().id(1L).name("Old").email("old@example.com").active(true).build();
        User other = User.builder().id(2L).name("Other").email("new@example.com").active(true).build();
        UserRequest request = new UserRequest();
        request.setName("New");
        request.setEmail("new@example.com");
        request.setActive(true);

        when(userRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(other));

        assertThatThrownBy(() -> userService.update(existing.getId(), request))
                .isInstanceOf(BookingException.class)
                .hasMessageContaining("User with email already exists");
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(99L))
                .isInstanceOf(BookingException.class)
                .hasMessageContaining("User not found");
    }
}
