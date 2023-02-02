package com.talan.empreintecarbone.service.impl;

import static org.mockito.Mockito.lenient;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.talan.empreintecarbone.model.User;
import com.talan.empreintecarbone.repository.UserRepository;
import com.talan.empreintecarbone.service.RouteService;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private UserRepository userRepository;
    private UserServiceImpl userServiceImpl;
    private RouteService routeService;
    @InjectMocks
    private User user;
    @InjectMocks
    private User user2;
    @InjectMocks
    private User user3;

    @BeforeEach
    void setUp() throws Exception {
        userRepository = Mockito.mock(UserRepository.class);
        routeService = Mockito.mock(RouteService.class);
        userServiceImpl = new UserServiceImpl(userRepository, routeService);
    }

    @Test
    void testFindAll() {
        // Setup.
        List<User> allUsers = new ArrayList<>();
        allUsers.add(user);
        allUsers.add(user2);
        allUsers.add(user3);

        lenient().when(userRepository.findAll()).thenReturn(allUsers);

        // Action.
        List<User> actualUsers = userServiceImpl.findAll();

        // Assert.
        Assertions.assertThat(actualUsers).isEqualTo(allUsers);
    }

    @Test
    void testFindOne() {
        // Setup.
        user.setId(UUID.randomUUID());
        Optional<User> optionalUser = Optional.of(user);
        UUID unknownId = UUID.randomUUID();

        lenient().when(userRepository.findById(user.getId())).thenReturn(optionalUser);

        // Action.
        User notFoundUser = userServiceImpl.findOne(unknownId.toString());
        User actualUser = userServiceImpl.findOne(user.getId().toString());

        // Assert.
        Assertions.assertThat(notFoundUser).isEqualTo(null);
        Assertions.assertThat(actualUser).isEqualTo(user);
    }

    @Test
    void testFindByUsername() {
        // Setup.
        user.setUsername("username");
        Optional<User> optionalUser = Optional.of(user);
        String unknownUsername = "unknown";

        lenient().when(userRepository.findByUsername(user.getUsername())).thenReturn(optionalUser);

        // Action.
        User notFoundUser = userServiceImpl.findByUsername(unknownUsername);
        User actualUser = userServiceImpl.findByUsername(user.getUsername());

        // Assert.
        Assertions.assertThat(notFoundUser).isEqualTo(null);
        Assertions.assertThat(actualUser).isEqualTo(user);
    }

    @Test
    void testSaveUser() {
        // Setup.
        lenient().when(userRepository.save(user)).thenReturn(user);

        // Action.
        User actualUser = userServiceImpl.save(user);

        // Assert.
        Assertions.assertThat(actualUser).isEqualTo(user);
    }

}
