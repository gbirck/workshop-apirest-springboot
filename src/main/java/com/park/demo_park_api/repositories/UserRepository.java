package com.park.demo_park_api.repositories;

import com.park.demo_park_api.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
