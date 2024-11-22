package com.reliaquest.api.service;

import com.reliaquest.server.model.MockEmployee;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final RestTemplate restTemplate;

    public List<MockEmployee> getAllEmployees() {
        return Collections.emptyList();
    }
}
