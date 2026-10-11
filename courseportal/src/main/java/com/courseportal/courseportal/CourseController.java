package com.courseportal.courseportal;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@CrossOrigin
public class CourseController {

    private final TopicService topicService;

    public CourseController(TopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping("/topics/{topicId}/courses")
    public List<Course> getAllCourses(@PathVariable int topicId) {
        return topicService.getAllCourses(topicId);
    }

    @GetMapping("/topic/{name}/courses")
    public List<Course> getAllCoursesByName(@PathVariable String name) {
        return topicService.getAllCoursesByName(name);
    }

    @GetMapping("/topics/{topicId}/courses/{id}")
    public ResponseEntity<Course> getCourse(@PathVariable int topicId, @PathVariable int id) {
        return topicService.getCourse(id)
                .filter(course -> course.getTopic().getId() == topicId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/topics/{topicId}/courses")
    public ResponseEntity<?> addCourse(
            @PathVariable int topicId,
            @RequestBody Course course) {
        if (!isValid(course)) {
            return ResponseEntity.badRequest().body("Course name and description are required.");
        }
        Optional<Course> saved = topicService.addCourse(topicId, course);
        if (!saved.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.get().getId())
                .toUri();
        return ResponseEntity.created(location).body(saved.get());
    }

    @PutMapping("/topics/{topicId}/courses/{id}")
    public ResponseEntity<?> updateCourse(
            @PathVariable int topicId,
            @PathVariable int id,
            @RequestBody Course course) {
        if (!isValid(course)) {
            return ResponseEntity.badRequest().body("Course name and description are required.");
        }
        Optional<Course> updated = topicService.updateCourse(topicId, id, course);
        return updated.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/topics/{topicId}/courses/{id}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable int topicId,
            @PathVariable int id) {
        Optional<Course> course = topicService.getCourse(id);
        if (!course.isPresent() || course.get().getTopic().getId() != topicId) {
            return ResponseEntity.notFound().build();
        }
        topicService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    private boolean isValid(Course course) {
        return course.getName() != null && !course.getName().trim().isEmpty()
                && course.getDescription() != null && !course.getDescription().trim().isEmpty();
    }
}
