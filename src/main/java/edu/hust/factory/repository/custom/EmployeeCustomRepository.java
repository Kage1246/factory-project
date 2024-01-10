package edu.hust.factory.repository.custom;

import edu.hust.factory.domain.Employee;
import edu.hust.factory.service.dto.EmployeeDTO;
import edu.hust.factory.service.model.PageFilterInput;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeCustomRepository {
    Page<Employee> search(PageFilterInput<EmployeeDTO> input, Pageable pageable);
}
