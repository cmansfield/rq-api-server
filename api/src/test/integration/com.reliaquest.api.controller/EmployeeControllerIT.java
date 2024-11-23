package com.reliaquest.api.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.reliaquest.api.annotation.RetryTest;
import com.reliaquest.api.model.Employee;
import io.micrometer.common.util.StringUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

// @ActiveProfiles("integration")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmployeeControllerIT {

    private String fullUri;

    @Value("${integration.server-uri}")
    private String serverUri;

    @Value("${integration.path}")
    private String pathUri;

    @LocalServerPort
    private int port;

    @Autowired
    private RestTemplate restTemplate;

    @BeforeAll
    public void setup() {
        fullUri = String.format("http://localhost:%s/%s", port, pathUri);
    }

    @Test
    @RetryTest(5)
    public void testGetAllEmployees() {
        var response = restTemplate.exchange(
                fullUri + "/employee", HttpMethod.GET, null, new ParameterizedTypeReference<List<Employee>>() {});
        List<Employee> body = response.hasBody() ? response.getBody() : null;

        assertFalse(CollectionUtils.isEmpty(body));
        assertEquals(50, body.size());
    }

    @Test
    @RetryTest(5)
    public void testGetEmployeesByNameSearch() throws UnsupportedEncodingException {
        var responseAll = restTemplate.exchange(
                fullUri + "/employee", HttpMethod.GET, null, new ParameterizedTypeReference<List<Employee>>() {});
        List<Employee> employees = responseAll.hasBody() ? responseAll.getBody() : null;
        assert employees != null;
        assert !employees.isEmpty();
        var employee = employees.get(0);

        var uri = String.format(
                "%s%s%s", fullUri, "/employee/search/", URLEncoder.encode(employee.name(), StandardCharsets.UTF_8));
        var response =
                restTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<List<Employee>>() {});
        List<Employee> body = response.hasBody() ? response.getBody() : null;

        assertFalse(CollectionUtils.isEmpty(body));

        var expected =
                employees.stream().filter(e -> e.name().equals(employee.name())).toList();
        assertEquals(expected.size(), body.size());
        assertTrue(body.stream().map(Employee::name).allMatch(n -> n.equals(employee.name())));
    }

    @Test
    @RetryTest(5)
    public void testGetEmployeeById() {
        var responseAll = restTemplate.exchange(
                fullUri + "/employee", HttpMethod.GET, null, new ParameterizedTypeReference<List<Employee>>() {});
        List<Employee> employees = responseAll.hasBody() ? responseAll.getBody() : null;
        assert employees != null;
        assert !employees.isEmpty();
        var employee = employees.get(0);

        var uri = String.format("%s%s%s", fullUri, "/employee/", employee.id());
        var response = restTemplate.exchange(uri, HttpMethod.GET, null, Employee.class);
        Employee result = response.hasBody() ? response.getBody() : null;

        assertNotNull(result);
        assertEquals(result, employee);
    }

    @Test
    @RetryTest(5)
    public void testGetHighestSalaryOfEmployees() {
        var responseAll = restTemplate.exchange(
                fullUri + "/employee", HttpMethod.GET, null, new ParameterizedTypeReference<List<Employee>>() {});
        List<Employee> employees = responseAll.hasBody() ? responseAll.getBody() : null;
        assert employees != null;
        assert !employees.isEmpty();
        var highestSalary =
                employees.stream().map(Employee::salary).max(Integer::compareTo).orElseThrow();

        var response = restTemplate.exchange(fullUri + "/highestSalary", HttpMethod.GET, null, Integer.class);
        Integer result = response.hasBody() ? response.getBody() : null;

        assertNotNull(result);
        assertEquals(result, highestSalary);
    }

    @Test
    @RetryTest(5)
    public void testGetTopTenHighestEarningEmployeeNames() {
        var responseAll = restTemplate.exchange(
                fullUri + "/employee", HttpMethod.GET, null, new ParameterizedTypeReference<List<Employee>>() {});
        List<Employee> employees = responseAll.hasBody() ? responseAll.getBody() : null;
        assert employees != null;
        assert !employees.isEmpty();
        employees.sort((e1, e2) -> e2.salary().compareTo(e1.salary()));
        var top = employees.subList(0, 10).stream().map(Employee::name).toList();

        var response = restTemplate.exchange(
                fullUri + "/topTenHighestEarningEmployeeNames",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<String>>() {});
        List<String> result = response.hasBody() ? response.getBody() : null;

        assertFalse(CollectionUtils.isEmpty(result));
        assertEquals(10, result.size());
        assertTrue(result.containsAll(top));
    }

    @Test
    @RetryTest(5)
    public void testCreateEmployee() {
        var employee = Employee.builder()
                .name("John Doe")
                .salary(56000)
                .age(32)
                .title("Sir Employee")
                .build();

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Employee> request = new HttpEntity<>(employee, headers);

        var response = restTemplate.exchange(fullUri + "/employee", HttpMethod.POST, request, Employee.class);
        Employee result = response.hasBody() ? response.getBody() : null;

        assertNotNull(result);
        assertNotNull(result.id());
        assertFalse(StringUtils.isBlank(result.email()));
        assertEquals(employee.name(), result.name());
        assertEquals(employee.salary(), result.salary());
        assertEquals(employee.age(), result.age());
        assertEquals(employee.title(), result.title());
    }

    @Test
    @RetryTest(5)
    public void testDeleteEmployeeById() {
        var responseAll = restTemplate.exchange(
                fullUri + "/employee", HttpMethod.GET, null, new ParameterizedTypeReference<List<Employee>>() {});
        List<Employee> employees = responseAll.hasBody() ? responseAll.getBody() : null;
        assert employees != null;
        assert !employees.isEmpty();
        var employee = employees.get(0);

        var uri = String.format("%s%s%s", fullUri, "/employee/", employee.id());
        var response = restTemplate.exchange(uri, HttpMethod.DELETE, null, Boolean.class);
        Boolean result = response.hasBody() ? response.getBody() : null;

        assertNotNull(result);
        assertTrue(result);
    }
}
