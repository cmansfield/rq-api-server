package com.reliaquest.api.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import com.reliaquest.server.model.CreateMockEmployeeInput;
import com.reliaquest.server.model.Response;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

@WebMvcTest(EmployeeController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmployeeControllerTest {

    private String controllerUri;

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private EmployeeService employeeService;

    @MockBean
    private RestTemplate restTemplate;

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

        doReturn(Collections.singletonList(employee)).when(employeeService).getAllEmployees();

        MvcResult result =
                mockMvc.perform(get(controllerUri)).andExpect(status().isOk()).andReturn();
        ObjectMapper mapper = new ObjectMapper();
        List<Employee> content = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});

        assertFalse(CollectionUtils.isEmpty(content));
        assertEquals(1, content.size());
        assertEquals(employee, content.get(0));

        verify(employeeService, times(1)).getAllEmployees();
    }

    @Test
    public void testGetAllEmployees_throwsException() throws Exception {
        String error = "Test exception";
        doThrow(new RuntimeException(error)).when(employeeService).getAllEmployees();

        MvcResult result = mockMvc.perform(get(controllerUri))
                .andExpect(status().is5xxServerError())
                .andReturn();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> content =
                mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});

        assertEquals(2, content.size());
        assertEquals("Failed to process request.", content.get("status"));
        assertEquals(error, content.get("error"));

        verify(employeeService, times(1)).getAllEmployees();
    }

    @Test
    public void testEmployeesByNameSearch() throws Exception {
        Employee employee = createMockEmployee();
        String uri = String.format("%s/search/%s", controllerUri, URLEncoder.encode("ohn D", StandardCharsets.UTF_8));

        doReturn(Collections.singletonList(employee)).when(employeeService).getAllEmployees();

        MvcResult result = mockMvc.perform(get(uri)).andExpect(status().isOk()).andReturn();
        ObjectMapper mapper = new ObjectMapper();
        List<Employee> content = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});

        assertFalse(CollectionUtils.isEmpty(content));
        assertEquals(1, content.size());
        assertEquals(employee, content.get(0));

        verify(employeeService, times(1)).getAllEmployees();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetEmployeeById() throws Exception {
        Employee employee = createMockEmployee();
        String uri = String.format("%s/%s", controllerUri, employee.id());
        var response = Response.handledWith(employee);
        var responseEntity = ResponseEntity.ok(response);

        when(restTemplate.exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        MvcResult result = mockMvc.perform(get(uri)).andExpect(status().isOk()).andReturn();
        ObjectMapper mapper = new ObjectMapper();
        Employee content = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});

        assertEquals(employee, content);
        verify(restTemplate, times(1)).exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class));
    }

    @Test
    public void testGetHighestSalaryOfEmployees() throws Exception {
        List<Employee> employees = Arrays.asList(createMockEmployee(), createMockEmployee2());

        doReturn(employees).when(employeeService).getAllEmployees();

        MvcResult result = mockMvc.perform(get(controllerUri + "/highestSalary"))
                .andExpect(status().isOk())
                .andReturn();
        ObjectMapper mapper = new ObjectMapper();
        Integer content = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});

        assertEquals(56000, content);
        verify(employeeService, times(1)).getAllEmployees();
    }

    @Test
    public void testGetTopTenHighestEarningEmployeeNames() throws Exception {
        List<Employee> employees = Arrays.asList(createMockEmployee(), createMockEmployee2());

        doReturn(employees).when(employeeService).getAllEmployees();

        MvcResult result = mockMvc.perform(get(controllerUri + "/topTenHighestEarningEmployeeNames"))
                .andExpect(status().isOk())
                .andReturn();
        ObjectMapper mapper = new ObjectMapper();
        List<String> content = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});

        assertFalse(CollectionUtils.isEmpty(content));
        assertEquals(2, content.size());
        assertTrue(content.contains("John Doe"));
        assertTrue(content.contains("Mr Smith"));

        verify(employeeService, times(1)).getAllEmployees();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateEmployee() throws Exception {
        Employee employee = createMockEmployee();
        var input = new CreateMockEmployeeInput();
        input.setName(employee.name());
        input.setSalary(employee.salary());
        input.setAge(employee.age());
        input.setTitle(employee.title());

        var response = Response.handledWith(employee);
        var responseEntity = ResponseEntity.ok(response);
        ObjectMapper mapper = new ObjectMapper();

        when(restTemplate.exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        MvcResult result = mockMvc.perform(post(controllerUri)
                        .content(mapper.writeValueAsString(input))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        Employee content = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {});

        assertEquals(employee, content);
        verify(restTemplate, times(1))
                .exchange(anyString(), eq(HttpMethod.POST), any(), any(ParameterizedTypeReference.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDeleteEmployee() throws Exception {
        Employee employee = createMockEmployee();
        String uri = String.format("%s/%s", controllerUri, employee.id());

        var response = Response.handledWith(true);
        var responseEntity = ResponseEntity.ok(response);

        doReturn(employee).when(employeeService).getEmployee(anyString());
        when(restTemplate.exchange(anyString(), any(), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(responseEntity);

        MvcResult result =
                mockMvc.perform(delete(uri)).andExpect(status().isOk()).andReturn();
        String content = result.getResponse().getContentAsString();

        assertEquals(employee.name(), content);

        verify(restTemplate, times(1))
                .exchange(anyString(), eq(HttpMethod.DELETE), any(), any(ParameterizedTypeReference.class));
        verify(employeeService, times(1)).getEmployee(anyString());
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

    private Employee createMockEmployee2() {
        String id = "12d6d4f6-1e7d-4035-bb31-70f38e7c8e12";
        return Employee.builder()
                .id(UUID.fromString(id))
                .name("Mr Smith")
                .salary(47000)
                .age(28)
                .title("Employee")
                .email("mr.smith@company.com")
                .build();
    }
}
