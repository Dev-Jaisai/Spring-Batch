package com.batch.springbatch.Spring.batch.repo;

import com.batch.springbatch.Spring.batch.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
}
