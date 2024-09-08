package com.courseportal.courseportal;



import javax.persistence.*;

import org.springframework.stereotype.Component;


@Table

class Topic
{
@Id    
int id;
String course;

public Topic()
{
    

}

  public Topic(int id, String course) 
  {
    super();
    this.id = id;
    this.course = course;
}


public int getId() {
    return id;
}
public void setId(int id) {
    this.id = id;
}
public String getCourse() {
    return course;
}
public void setCourse(String course) {
    this.course = course;
}

}