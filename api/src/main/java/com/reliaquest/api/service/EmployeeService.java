package com.reliaquest.api.service;

import com.reliaquest.api.model.DeleteEmployeeResponseType;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeResponseType;
import com.reliaquest.api.model.EmployeesResponseType;
import com.reliaquest.server.model.CreateMockEmployeeInput;
import com.reliaquest.server.model.DeleteMockEmployeeInput;
import com.reliaquest.server.model.Response;
import io.micrometer.common.util.StringUtils;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final RestTemplate restTemplate;

    /**
     * Retrieves a list of all {@link Employee employees} from the version 1 API.
     *
     * @return    Returns a list of {@link Employee} objects
     */
    public List<Employee> getAllEmployees() {
        return get("/employee", new EmployeesResponseType());
    }

    /**
     * This method returns a list of {@link Employee employees} from the
     * version 1 API whose names match the specified <b>searchString</b>.
     *
     * @param searchString    The search string to filter employees by
     * @return                Returns a list of {@link Employee} objects
     */
    public List<Employee> getEmployeesByNameSearch(@NonNull String searchString) {
        if (StringUtils.isBlank(searchString)) {
            throw new IllegalArgumentException("Search string cannot be empty.");
        }
        var search = URLDecoder.decode(searchString, StandardCharsets.UTF_8).toLowerCase();
        var employees = getAllEmployees();
        return employees.stream()
                .filter(Objects::nonNull)
                .filter(e -> StringUtils.isNotBlank(e.name()))
                .filter(e -> e.name().toLowerCase().contains(search))
                .toList();
    }

    /**
     * This method retrieves an {@link Employee employee} from the version 1 API
     * whose ID matches the specified <b>id</b>.
     *
     * <p>The ID must be a valid {@linkplain UUID}.
     *
     * @param id    The ID of the employee to retrieve
     * @return      Returns an instance of {@link Employee}
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public Employee getEmployee(@NonNull String id) {
        UUID.fromString(id);
        return get("/employee/" + id, new EmployeeResponseType());
    }

    /**
     * This method retrieves the <em>highest</em> salary of all employees from the version 1 API.
     *
     * @return    Returns the highest salary integer of all employees
     */
    public Integer getHighestSalary() {
        return getAllEmployees().stream()
                .map(Employee::salary)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(0);
    }

    /**
     * This method retrieves the <em>top ten</em> highest earning employee names from the version 1 API.
     *
     * @return    Returns a list of the top ten highest earning employee names
     */
    public List<String> getTopTenHighestEarningEmployeeNames() {
        return getAllEmployees().stream()
                .filter(Objects::nonNull)
                .filter(e -> e.salary() != null)
                .sorted((e1, e2) -> e2.salary().compareTo(e1.salary()))
                .limit(10)
                .map(Employee::name)
                .toList();
    }

    /**
     * This method creates a new {@link Employee employee} leveraging the version 1 API
     * by supplying the expected <b>input</b> parameters.
     *
     * @param input    The input fields required to create the employee
     * @return         Returns an instance of the created {@link Employee}
     * @see CreateMockEmployeeInput
     */
    public Employee createEmployee(@NonNull CreateMockEmployeeInput input) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreateMockEmployeeInput> request = new HttpEntity<>(input, headers);

        var response = restTemplate
                .exchange("/employee", HttpMethod.POST, request, new EmployeeResponseType())
                .getBody();
        if (response == null || StringUtils.isNotBlank(response.error())) {
            throw new IllegalStateException("Unable to create employee.");
        }

        return response.data();
    }

    /**
     * This method deletes an {@link Employee employee} from the version 1 API
     * whose ID matches the specified <b>id</b>.
     *
     * <p>The ID must be a valid {@linkplain UUID}.
     *
     * @param id    The ID of the employee to delete
     * @return      Returns the name of the deleted employee
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public String deleteEmployee(@NonNull String id) {
        UUID.fromString(id);
        var employee = getEmployee(id);
        if (employee == null) {
            throw new MissingResourceException("Employee not found.", EmployeeService.class.getName(), id);
        }
        if (StringUtils.isBlank(employee.name())) {
            throw new IllegalArgumentException("Employee name cannot be empty.");
        }

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var deleteInput = new DeleteMockEmployeeInput();
        deleteInput.setName(employee.name());
        HttpEntity<DeleteMockEmployeeInput> request = new HttpEntity<>(deleteInput, headers);

        var result = restTemplate
                .exchange("/employee", HttpMethod.DELETE, request, new DeleteEmployeeResponseType())
                .getBody();
        if (result != null && Boolean.TRUE.equals(result.data())) {
            return employee.name();
        }

        log.debug("Unable to delete employee: {}", employee.id());
        throw new MissingResourceException("Unable to delete employee", EmployeeService.class.getName(), id);
    }

    /**
     * This method makes a GET REST call to the specified <b>path</b> and returns the response
     * cast as the specified generic <b>type</b> <em>T</em>. This method will unpackage the request
     * and return an instance of the generic type <em>T</em>.
     *
     * <p>All requests from the version 1 API are wrapped in a {@link Response} object.
     *
     * @param path      The path to be appended to the base URL
     * @param type      The parameterized type to cast the response to
     * @return          Returns an instance of the generic type <em>T</em>
     * @param <T>       The generic type to cast the response to
     */
    private <T> T get(@NonNull String path, @NonNull ParameterizedTypeReference<Response<T>> type) {
        var response = restTemplate.exchange(path, HttpMethod.GET, null, type);
        var body = response.hasBody() ? response.getBody() : null;
        if (body == null || StringUtils.isNotBlank(body.error())) {
            throw new MissingResourceException("Unable to retrieve resource.", EmployeeService.class.getName(), path);
        }
        return body.data();
    }
}
