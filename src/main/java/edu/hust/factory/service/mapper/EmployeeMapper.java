package edu.hust.factory.service.mapper;

import edu.hust.factory.domain.Employee;
import edu.hust.factory.service.dto.EmployeeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;

@Component
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface EmployeeMapper extends EntityMapper<EmployeeDTO, Employee> {
    @Mapping(target = "employeeCode", ignore = true)
    void updateFromDTO(@MappingTarget Employee entity, EmployeeDTO dto);
}
