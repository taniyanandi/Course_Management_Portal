package com.courseportal.courseportal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class CourseportalApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void courseAndTopicEndpointsSupportCompleteCrudLifecycle() throws Exception {
        MvcResult topicResult = mockMvc.perform(post("/topics")
                        .contentType("application/json")
                        .content("{\"course\":\"Computer Science\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.course").value("Computer Science"))
                .andReturn();

        Object topicIdValue = com.jayway.jsonpath.JsonPath.read(
                topicResult.getResponse().getContentAsString(), "$.id");
        String topicId = topicIdValue.toString();

        mockMvc.perform(get("/topics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].course").value("Computer Science"));

        mockMvc.perform(get("/topics/{id}", topicId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.course").value("Computer Science"));

        MvcResult courseResult = mockMvc.perform(post("/topics/{id}/courses", topicId)
                        .contentType("application/json")
                        .content("{\"name\":\"Distributed Systems\",\"description\":\"Learn to design resilient systems.\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Distributed Systems"))
                .andExpect(jsonPath("$.topic.id").value(Integer.parseInt(topicId)))
                .andReturn();

        Object courseIdValue = com.jayway.jsonpath.JsonPath.read(
                courseResult.getResponse().getContentAsString(), "$.id");
        String courseId = courseIdValue.toString();

        mockMvc.perform(get("/topics/{topicId}/courses", topicId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Distributed Systems"));

        mockMvc.perform(get("/topic/{name}/courses", "Computer Science"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Distributed Systems"));

        mockMvc.perform(get("/topics/{topicId}/courses/{id}", topicId, courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Learn to design resilient systems."));

        mockMvc.perform(put("/topics/{topicId}/courses/{id}", topicId, courseId)
                        .contentType("application/json")
                        .content("{\"name\":\"Reliable Systems\",\"description\":\"Updated course description.\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Reliable Systems"));

        mockMvc.perform(delete("/topics/{topicId}/courses/{id}", topicId, courseId))
                .andExpect(status().isNoContent());

        MvcResult secondCourseResult = mockMvc.perform(post("/topics/{id}/courses", topicId)
                        .contentType("application/json")
                        .content("{\"name\":\"Cloud Computing\",\"description\":\"Explore cloud platforms.\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        Object secondCourseId = com.jayway.jsonpath.JsonPath.read(
                secondCourseResult.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(put("/topics/{id}", topicId)
                        .contentType("application/json")
                        .content("{\"course\":\"Software Engineering\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.course").value("Software Engineering"));

        mockMvc.perform(delete("/topics/{id}", topicId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/topics/{id}", topicId))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/topics/{topicId}/courses/{id}", topicId, secondCourseId))
                .andExpect(status().isNotFound());
    }

    @Test
    void rejectsInvalidInputAndUnknownParentResources() throws Exception {
        mockMvc.perform(post("/topics")
                        .contentType("application/json")
                        .content("{\"course\":\"  \"}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/topics/{id}/courses", 999999)
                        .contentType("application/json")
                        .content("{\"name\":\"Course\",\"description\":\"Description\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void legacyTopicDeleteEndpointRemainsFunctional() throws Exception {
        MvcResult topicResult = mockMvc.perform(post("/topics")
                        .contentType("application/json")
                        .content("{\"course\":\"Legacy topic\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        Object topicId = com.jayway.jsonpath.JsonPath.read(
                topicResult.getResponse().getContentAsString(), "$.id");

        mockMvc.perform(get("/topics/delete/{course}", "Legacy topic"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/topics/{id}", topicId))
                .andExpect(status().isNotFound());
    }
}
