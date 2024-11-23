package com.reliaquest.api.model;

import com.reliaquest.server.model.Response;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;

public class EmployeesResponseType extends ParameterizedTypeReference<Response<List<Employee>>> {}
