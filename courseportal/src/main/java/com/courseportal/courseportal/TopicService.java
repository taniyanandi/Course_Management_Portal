package com.courseportal.courseportal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class TopicService {
    
   Topic topic1;

   
   TopicRepository topicRepository;

     CourseRepository courseRepository;







   // List<Topic> topics= new ArrayList<>(Arrays.asList(new Topic(1, "Spring"),new Topic(2,"Java")));
    public void TopicService(TopicRepository topicRepository,CourseRepository courseRepository)
{
    this.topicRepository=topicRepository;
    this.courseRepository = courseRepository;
    
}
       List <Topic> topics  = new ArrayList<>();
       List<Course> courses = new ArrayList<>();
    
    
        
        public List<Topic> getAllTopics() 
        {
            Topic topic1= new Topic();
            topic1.setId(1);
            topic1.setCourse("Java");
             
            topics.add(topic1);
            
            if(topicRepository!=null)
            {
            topicRepository.findAll().forEach(topics::add);
            

            topicRepository.save(topic1);
            }
            
            return topics;
            
        }
    
        public Topic getTopicById(int id) {
            
           // return  (Topic)  topics.stream().filter(t-> t.getId()  == id).findFirst().get();
           for(Topic t: topics)
           {
               if(t.getId()==id)
               {
                   return t;
               }
              
   
           }
           return null;
       
        }
    
        public void addTopic(Topic topic) 
        {
            topics.add(topic);
            if(topicRepository!=null)
            {
            
            topicRepository.save(topic);
            }
            
        }
        public void delTopic(String course) 
        {
           /* System.out.println("Taniya- "+course );
            System.out.println("Taniya- "+topics.get(0) );
            System.out.println("Taniya1 "+topics.indexOf("Spring") );*/

           //Topic topic= topicRepository.getTopicByCourse(course);
           Topic topic= topicRepository.findByCourse(course);







            if(topic !=null)
            topics.remove(topic);
            
            
        }
    
        public void updateTopicTopic(int id, Topic topic)
         {
            for(int i=0;i<topics.size();i++)
            {
                if(topics.get(i).getId()==id)
                {
                    topics.set(i, topic);
                    return;
                }
            }
        }
          
     
    
    public List<Course> getAllCourses(int id) {
        Course course=new Course();
        Topic topic=new Topic();
        course.setId(id);
        course.setName("SpringJpa");
        course.setDescription("A complete introduction to Spring ");
        topic.setId(2);
        topic.setCourse("springData");
        course.setTopic(topic);
        courses.add(course);
        topics.add(topic);
       

        
        if(courseRepository!=null)
        courseRepository.findByTopicId(id).forEach(courses::add);
        List<Course> allcourses=new ArrayList<>();
        //allcourses=courses.stream().filter(c -> c.getId()==id).map(course).collect(Collectors.toList);
        for(Course c: courses)
        {
            if(c.getId()==id)
            {
                allcourses.add(c);
            }

        }

        

        return allcourses;
      }
    
      public List<Course> getAllCoursesByName(String name)
      {
        
        if(courseRepository!=null)
        courseRepository.findByTopicName(name).forEach(courses::add);
        return courses;
    
      }
    
  
    
      public void addCourse(Course course){
        if(courseRepository!=null)
        courseRepository.save(course);
        courses.add(course);
        
      }
    
      public void updateCourse(Course course) {
         courseRepository.save(course);
      }
    
      /**
     * @param id
     */
    public void deleteCourse(int id) {
        //Course index=new Course();
    
        courses.remove(0);
        courseRepository.deleteById(id);
      }

    public Course getCourse(int id) {
       return courses.get(id);
    }

  
}

