package com.courseportal.courseportal;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Integer> {

    Optional<Topic> findByCourseIgnoreCase(String course);
}
