package com.reliaquest.api

import com.reliaquest.api.model.Employee
import com.reliaquest.api.service.EmployeeService
import org.mockito.Mock
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

import java.util.stream.IntStream

class EmployeeServiceTest extends Specification {

    private EmployeeService employeeService

    @Mock
    private RestTemplate restTemplate

    void setup() {
        employeeService = Spy(new EmployeeService(restTemplate))
    }

    def "test get employees by name search" () {
        given:
        var employees = getEmployees(5)
        (error == null ? 1 : 0) * employeeService.getAllEmployees() >> employees

        when:
        var result = employeeService.getEmployeesByNameSearch(search)

        then:
        if (expected != null) {
            result.size() == expected
            return
        }
        def e = thrown(IllegalArgumentException)
        e.message == error

        where:
        search      | error                             | expected
        "1"         | null                              | 1
        "mploy"     | null                              | 5
        "loyee%203" | null                              | 1
        ""          | "Search string cannot be empty."  | null
    }

    def "test get highest salary" () {
        given:
        var employees = getEmployees(numEmployees)
        1 * employeeService.getAllEmployees() >> employees

        when:
        var result = employeeService.getHighestSalary()

        then:
        result == expected

        where:
        numEmployees    | expected
        0               | 0
        1               | 1000
        2               | 2000
        3               | 3000
        4               | 4000
        5               | 5000
        6               | 6000
    }

    def "test get top ten highest earning employee names" () {
        given:
        var employees = getEmployees(15)
        1 * employeeService.getAllEmployees() >> employees

        when:
        var result = employeeService.getTopTenHighestEarningEmployeeNames()

        then:
        result.size() == 10
        result == ["Employee 15", "Employee 14", "Employee 13", "Employee 12", "Employee 11", "Employee 10", "Employee 9", "Employee 8", "Employee 7", "Employee 6"]
    }

    private List<Employee> getEmployees(int count) {
        return IntStream.rangeClosed(1, count)
            .mapToObj { i -> new Employee(UUID.randomUUID(), "Employee $i", 1000 * i, 25 + i, "Title $i", "$i@company.com") }
            .toList()
    }
}
