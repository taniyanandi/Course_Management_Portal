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
public class TopicController {

    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping("/topics")
    public List<Topic> getAllTopics() {
        return topicService.getAllTopics();
    }

    @GetMapping("/topics/{id}")
    public ResponseEntity<Topic> getTopicById(@PathVariable int id) {
        return topicService.getTopicById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/topics")
    public ResponseEntity<?> addTopic(@RequestBody Topic topic) {
        if (topic.getCourse() == null || topic.getCourse().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Topic name is required.");
        }
        Topic savedTopic = topicService.addTopic(topic);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedTopic.getId())
                .toUri();
        return ResponseEntity.created(location).body(savedTopic);
    }

    @PutMapping("/topics/{id}")
    public ResponseEntity<?> updateTopic(@PathVariable int id, @RequestBody Topic topic) {
        if (topic.getCourse() == null || topic.getCourse().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Topic name is required.");
        }
        Optional<Topic> updated = topicService.updateTopic(id, topic);
        return updated.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/topics/{id}")
    public ResponseEntity<Void> deleteTopic(@PathVariable int id) {
        return topicService.deleteTopic(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/topics/delete/{course}")
    public ResponseEntity<Void> deleteTopicByName(@PathVariable String course) {
        return topicService.deleteTopicByName(course)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
