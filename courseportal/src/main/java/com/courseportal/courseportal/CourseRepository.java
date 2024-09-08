package com.courseportal.courseportal;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

//import antlr.collections.List;

@Repository
public interface CourseRepository extends CrudRepository<Course, Integer> {
    public List<Course> findByTopicId(int id);
    public List<Course> findByTopicName(String topicName);
    public void deleteById(int id);
 }