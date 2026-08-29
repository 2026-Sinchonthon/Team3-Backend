package com.sinchonthon.team3_backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sinchonthon.team3_backend.domain.user.User;
import com.sinchonthon.team3_backend.exception.ApiException;
import com.sinchonthon.team3_backend.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock UserRepository users;
    @InjectMocks UserService userService;

    @Test
    void deleteAccountDeletesAuthenticatedUser() {
        User user = new User("member@example.com");
        when(users.findById(1L)).thenReturn(Optional.of(user));
        userService.deleteAccount(1L);
        verify(users).deleteCommentsByUserId(1L);
        verify(users).deleteTipsByUserId(1L);
        verify(users).delete(user);
    }

    @Test
    void deleteAccountFailsWhenUserDoesNotExist() {
        when(users.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.deleteAccount(1L))
                .isInstanceOfSatisfying(ApiException.class,
                        exception -> assertThat(exception.status()).isEqualTo(HttpStatus.NOT_FOUND));
    }
}
