package com.reliaquest.api.service;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeesResponseType;
import com.reliaquest.server.model.Response;
import io.micrometer.common.util.StringUtils;
import java.util.List;
import java.util.MissingResourceException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final RestTemplate restTemplate;

    public List<Employee> getAllEmployees() {
        return get("/employee", new EmployeesResponseType());
    }

    private <T> T get(@NonNull String path, @NonNull ParameterizedTypeReference<Response<T>> type) {
        var response = restTemplate.exchange(path, HttpMethod.GET, null, type);
        var body = response.hasBody() ? response.getBody() : null;
        if (body == null || StringUtils.isNotBlank(body.error())) {
            throw new MissingResourceException("Unable to retrieve resource.", EmployeeService.class.getName(), path);
        }
        return body.data();
    }
}
