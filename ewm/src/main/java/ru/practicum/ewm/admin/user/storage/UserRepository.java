package ru.practicum.ewm.admin.user.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.admin.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
