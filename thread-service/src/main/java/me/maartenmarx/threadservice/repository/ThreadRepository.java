package me.maartenmarx.threadservice.repository;

import me.maartenmarx.threadservice.model.Thread;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ThreadRepository extends JpaRepository<Thread, Long> {
    List<Thread> findByUserId(String id);
}
