# ReliaQuest Coding Challenge
#### Coding challenge completed by: *Christopher Mansfield*
<br>

### Overview
This project is a RESTful API server that provides *version 2* for the employee endpoints.
Because this service relies on the information provided by the *version 1* service, this
service is effectively a **decorator** for the server deployment.

This means this project extends the functionality of version 1 without modifying the original
codebase. That comes with a few caveats, including but not limited to: 
- This service is not standalone
- Increased network latency
- Decreased Reliability
- More data is queried for the endpoints
- Prevents optimizations such as caching, or RPCs
- Changes to the original server may break this service
- The modules are tightly coupled

For example, the `EmployeeService::getHighestSalary` method's  space complexity is **O(n)** because
retrieve all the employees before finding the highest salary.

We cannot introduce caching because we don't have an independent datastore, and we're providing long term support
for version 1. This means we must query the data every time we need it because there's a chance an employee could
have been created or deleted using the version 1 api. 

<br>

### Integration Tests
The integration tests are located in the `test.integration` directory and end with the `IT` postfix. These
tests are meant to test end-to-end functionality of the API. This requires the server to be running in 
order to run these tests.

Because the server is rate limited, the integration tests are *not* run by default and can take a few minutes
to complete. The tests will retry and wait a few times before failing. 

To run the integration tests, first start the server.
```bash
./gradlew :server:bootRun
```

Either use the `Integration API Tests` run configuration in IntelliJ IDEA or run the following:

```bash
./gradlew :api:test -Dtest.profile=integration
```
<br>

### Unit Tests
The unit tests are standalone and do not require any deployments. They are located 
in the `test.unit` directory and end with the `Test` postfix. There are two different testing frameworks 
utilized for the unit tests: **Spock** and **Mockito**. Usually, only one of testing frameworks is used, but I 
wanted to demonstrate both in this project. The Spock tests are located in the `test.unit.groovy` package and the Mockito 
tests are located in the `test.unit.mockito` package. 

The Unit tests are automatically run when the project is built, or with the following command:

```bash
./gradlew :api:test
```
<br>

### Final Thoughts
I enjoyed working on this challenge and I hope you enjoy reviewing it. I look forward to discussing my design choices 
with you and would appreciate feedback if any.