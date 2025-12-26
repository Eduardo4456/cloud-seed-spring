package com.project.cloudseed.repository;

import com.project.cloudseed.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {}