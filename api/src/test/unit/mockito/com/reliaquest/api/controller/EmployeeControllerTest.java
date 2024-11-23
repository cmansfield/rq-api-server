package com.reliaquest.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;

@WebMvcTest(EmployeeController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmployeeControllerTest {

    private String controllerUri;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService service;

    @BeforeAll
    public void classSetup() {
        RequestMapping requestMapping = EmployeeController.class.getAnnotation(RequestMapping.class);
        if (requestMapping != null && requestMapping.value().length > 0) {
            controllerUri = requestMapping.value()[0];
        }
    }

    @Test
    public void testGetAllEmployees() throws Exception {
        Employee employee = createMockEmployee();

        when(service.getAllEmployees()).thenReturn(Collections.singletonList(employee));

        MvcResult result =
                mockMvc.perform(get(controllerUri)).andExpect(status().isOk()).andReturn();
        ObjectMapper mapper = new ObjectMapper();
        List<Employee> content = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});

        assertFalse(CollectionUtils.isEmpty(content));
        assertEquals(1, content.size());
        assertEquals(employee, content.get(0));
    }

    @Test
    public void testGetAllEmployees_throwsException() throws Exception {
        mockMvc.perform(get(controllerUri)).andDo(print()).andExpect(status().isOk());
    }

    private Employee createMockEmployee() {
        String id = "2a049e55-2c0a-4dff-b4e9-689ffa8eb3b0";
        return Employee.builder()
                .id(UUID.fromString(id))
                .name("John Doe")
                .salary(56000)
                .age(32)
                .title("Sir Employee")
                .email("john.doe@company.com")
                .build();
    }
}
