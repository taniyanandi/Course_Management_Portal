package com.courseportal.courseportal;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

    List<Course> findByTopicId(int topicId);

    List<Course> findByTopicCourseIgnoreCase(String course);

    void deleteByTopicId(int topicId);
}
