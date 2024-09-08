package com.courseportal.courseportal;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import com.courseportal.courseportal.Topic;

//import io.micrometer.common.lang.Nullable;


@Repository

public interface TopicRepository extends JpaRepository<Topic, Integer>
{
     
     public Topic save(Topic topic);

    //public Topic getTopicbyCourse(String course);

    public Topic getTopicByCourse(String course);

public Topic findByCourse(String course);
}