package com.courseportal.courseportal;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Component
@RestController
class TopicController
{
    @Autowired
    @Qualifier("topicService")
    private TopicService topicService;
  
    TopicRepository topicRepository;
    public void TopicController(TopicRepository topicRepository1)
    {
        this.topicRepository=topicRepository1;
    }

    
    //helps in dependency injection

    @CrossOrigin
    @GetMapping("/topics")
    public List<Topic> getalltopicString()
    {
        return topicService.getAllTopics();

    }

    @GetMapping("topics/{id}")
    public Topic getTopicbyId(@PathVariable int id)
    {
        return topicService.getTopicById(id);
    }
    @GetMapping("topics/delete/{course}")
    public void deleteCourse(@PathVariable String course)
    {
         topicService.delTopic(course);
    }

    @PostMapping("/topics")
    public void addTopic(@RequestBody Topic topic)
    {
        topicService.addTopic(topic);

    }
    @RequestMapping(method= RequestMethod.PUT, value = "/topics/{id}")
    public void updateTopic(@RequestBody Topic topic , @PathVariable int id)
    {
        topicService.updateTopicTopic(id,topic);


    }

}