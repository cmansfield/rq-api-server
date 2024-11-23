package com.reliaquest.api.controller;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import com.reliaquest.server.controller.MockEmployeeController;
import com.reliaquest.server.model.CreateMockEmployeeInput;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/employee")
@RequiredArgsConstructor
public class EmployeeController implements IEmployeeController<Employee, CreateMockEmployeeInput> {

    private final EmployeeService employeeService;

    /**
     * Get a list of <em>all</em> employees.
     * This controller sources its information from api version 1 for employees.
     * See {@link MockEmployeeController} for more information about version 1.
     *
     * @return      Returns a list of {@link Employee} objects
     */
    @Override
    @GetMapping()
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    /**
     * Get a list of employees whose names match the specified <b>searchString</b>.
     *
     * @param searchString  The search string to filter employees by
     * @return              Returns a list of {@link Employee} objects
     */
    @Override
    @GetMapping("/search/{searchString}")
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable String searchString) {
        return ResponseEntity.ok(employeeService.getEmployeesByNameSearch(searchString));
    }

    /**
     * Get an employee by their unique identifier.
     *
     * <p><b>id</b> must be a valid {@linkplain UUID}.
     *
     * @param id    The unique identifier of the employee
     * @return      Returns an {@link Employee} object
     */
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {
        return ResponseEntity.ok(employeeService.getEmployee(id));
    }

    /**
     * Get the <em>highest</em> salary of all employees.
     *
     * @return      Returns an integer value of highest salary of all employees
     */
    @Override
    @GetMapping("/highestSalary")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        return ResponseEntity.ok(employeeService.getHighestSalary());
    }

    /**
     * Get the <em>top 10</em> highest salary earning employee names.
     *
     * @return      Returns a {@linkplain List} of employee name {@linkplain String strings}
     */
    @Override
    @GetMapping("/topTenHighestEarningEmployeeNames")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        return ResponseEntity.ok(employeeService.getTopTenHighestEarningEmployeeNames());
    }

    /**
     * Create a new employee.
     *
     * @param employeeInput     The {@link CreateMockEmployeeInput input} data to create a new employee
     * @return                  Returns an {@link Employee} object
     */
    @Override
    @PostMapping()
    public ResponseEntity<Employee> createEmployee(@RequestBody CreateMockEmployeeInput employeeInput) {
        var employee = employeeService.createEmployee(employeeInput);
        return ResponseEntity.status(employee == null ? HttpStatus.BAD_REQUEST : HttpStatus.CREATED)
                .body(employee);
    }

    /**
     * Delete an employee by their unique identifier.
     *
     * <p><b>id</b> must be a valid {@linkplain UUID}.
     *
     * @param id    The unique identifier of the employee to delete
     * @return      Returns a {@linkplain String} of the deleted employee's name
     */
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable String id) {
        return ResponseEntity.ok(employeeService.deleteEmployee(id));
    }
}
