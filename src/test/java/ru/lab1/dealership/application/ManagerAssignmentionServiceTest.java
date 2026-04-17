package ru.lab1.dealership.application;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import ru.lab1.dealership.domain.exception.DomainValidationException;
import ru.lab1.dealership.domain.user.User;
import ru.lab1.dealership.infrastructure.repository.UserRepository;

class ManagerAssignmentionServiceTest {

  @Test
  void shouldAssignManager() {
    var repo = new UserRepository();

    var m1 = new User(UUID.randomUUID(), "M1", User.Role.MANAGER);
    var m2 = new User(UUID.randomUUID(), "M2", User.Role.MANAGER);

    repo.save(m1);
    repo.save(m2);

    var assignment = new ManagerAssignmentService(repo, new Random(1));
    var chosen = assignment.assignManager();

    assertEquals(User.Role.MANAGER, chosen.getRole());
    assertTrue(chosen == m1 || chosen == m2);
  }

  @Test
  void shouldThrowIfNoManagers() {
    var repo = new UserRepository();
    var assignment = new ManagerAssignmentService(repo, new Random(1));

    assertThrows(DomainValidationException.class, assignment::assignManager);
  }
}
