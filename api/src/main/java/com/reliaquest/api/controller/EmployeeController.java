package com.reliaquest.api.controller;

import com.reliaquest.api.service.EmployeeService;
import com.reliaquest.server.controller.MockEmployeeController;
import com.reliaquest.server.model.CreateMockEmployeeInput;
import com.reliaquest.server.model.MockEmployee;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/employee")
@RequiredArgsConstructor
public class EmployeeController implements IEmployeeController<MockEmployee, CreateMockEmployeeInput> {

    private final EmployeeService employeeService;

    /**
     * Get a list of all employees.
     * This controller sources its information from api version 1 for employees. {@link MockEmployeeController}
     *
     * @author      Christopher Mansfield
     * @return      Returns a list of {@link MockEmployee} objects
     */
    @Override
    @GetMapping()
    public ResponseEntity<List<MockEmployee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    /**
     *
     *
     * @param searchString
     * @return
     */
    @Override
    @GetMapping("/search/{searchString}")
    public ResponseEntity<List<MockEmployee>> getEmployeesByNameSearch(@PathVariable String searchString) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    /**
     *
     *
     * @param id
     * @return
     */
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<MockEmployee> getEmployeeById(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    /**
     *
     * @return
     */
    @Override
    @GetMapping("/highestSalary")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    /**
     *
     *
     * @return
     */
    @Override
    @GetMapping("/topTenHighestEarningEmployeeNames")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    /**
     *
     *
     * @param employeeInput
     * @return
     */
    @Override
    @PostMapping()
    public ResponseEntity<MockEmployee> createEmployee(@RequestBody CreateMockEmployeeInput employeeInput) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    /**
     *
     *
     * @param id
     * @return
     */
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
