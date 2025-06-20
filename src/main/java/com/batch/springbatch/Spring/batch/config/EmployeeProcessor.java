package com.batch.springbatch.Spring.batch.config;

import com.batch.springbatch.Spring.batch.entity.Employee;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

public class EmployeeProcessor implements ItemProcessor<Employee, Employee> {

    @Override
    public Employee process(Employee employee) throws Exception {
        // Hike salary by 10%
        double newSalary = employee.getSalary() * 1.10;
        employee.setSalary(newSalary);
        return employee;
    }
}
