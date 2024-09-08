package com.courseportal.courseportal;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


    @RestController
public class CourseController {
    
    
    @Autowired
    private TopicService topicService;
  


  

  @RequestMapping("/topics/{id}/courses")
  public List<Course> getAllCourses(@PathVariable int id) {
    return topicService.getAllCourses(id);
  }

  @RequestMapping("/topic/{name}/courses")
 public List<Course>  getAllCoursesByName(@PathVariable String name)
 {
  return topicService.getAllCoursesByName(name);
  //return null;
  
 }

  @RequestMapping("/topics/{topicId}/courses/{id}")
  public Course getCourse(@PathVariable int id) {
    return topicService.getCourse(id);
  }

  @RequestMapping(method = RequestMethod.POST, value = "/topics/{topicId}/courses")
  public void addCourse(@RequestBody Course course, @PathVariable String topicId) {
    course.setTopic(new Topic());
    topicService.addCourse(course);
  }

  @RequestMapping(method = RequestMethod.PUT, value = "/topics/{topicId}/courses/{id}")
  public void updateCourse(@RequestBody Course course, @PathVariable int topicId, @PathVariable int id) {
    course.setTopic(new Topic());
    topicService.updateCourse(course);
  }

  @RequestMapping(method = RequestMethod.DELETE, value = "/topics/{topicId}/courses/{id}")
  public void deleteCourse(@PathVariable int id) {
    topicService.deleteCourse(id);
  }

    
}
