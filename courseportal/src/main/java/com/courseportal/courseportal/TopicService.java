package com.courseportal.courseportal;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TopicService {

    private final TopicRepository topicRepository;
    private final CourseRepository courseRepository;

    public TopicService(TopicRepository topicRepository, CourseRepository courseRepository) {
        this.topicRepository = topicRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional(readOnly = true)
    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Topic> getTopicById(int id) {
        return topicRepository.findById(id);
    }

    public Topic addTopic(Topic topic) {
        topic.setId(0);
        return topicRepository.save(topic);
    }

    public Optional<Topic> updateTopic(int id, Topic updatedTopic) {
        return topicRepository.findById(id).map(topic -> {
            topic.setCourse(updatedTopic.getCourse());
            return topicRepository.save(topic);
        });
    }

    public boolean deleteTopic(int id) {
        if (!topicRepository.existsById(id)) {
            return false;
        }
        courseRepository.deleteByTopicId(id);
        topicRepository.deleteById(id);
        return true;
    }

    public boolean deleteTopicByName(String course) {
        return topicRepository.findByCourseIgnoreCase(course)
                .map(topic -> deleteTopic(topic.getId()))
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public List<Course> getAllCourses(int topicId) {
        return courseRepository.findByTopicId(topicId);
    }

    @Transactional(readOnly = true)
    public List<Course> getAllCoursesByName(String name) {
        return courseRepository.findByTopicCourseIgnoreCase(name);
    }

    @Transactional(readOnly = true)
    public Optional<Course> getCourse(int id) {
        return courseRepository.findById(id);
    }

    public Optional<Course> addCourse(int topicId, Course course) {
        return topicRepository.findById(topicId).map(topic -> {
            course.setId(0);
            course.setTopic(topic);
            return courseRepository.save(course);
        });
    }

    public Optional<Course> updateCourse(int topicId, int id, Course updatedCourse) {
        Optional<Topic> topic = topicRepository.findById(topicId);
        Optional<Course> course = courseRepository.findById(id);
        if (!topic.isPresent() || !course.isPresent()) {
            return Optional.empty();
        }

        Course existingCourse = course.get();
        existingCourse.setName(updatedCourse.getName());
        existingCourse.setDescription(updatedCourse.getDescription());
        existingCourse.setTopic(topic.get());
        return Optional.of(courseRepository.save(existingCourse));
    }

    public boolean deleteCourse(int id) {
        if (!courseRepository.existsById(id)) {
            return false;
        }
        courseRepository.deleteById(id);
        return true;
    }
}
