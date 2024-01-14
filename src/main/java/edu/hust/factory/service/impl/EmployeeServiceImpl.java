package edu.hust.factory.service.impl;

import com.google.api.client.http.HttpStatusCodes;
import edu.hust.factory.domain.Employee;
import edu.hust.factory.domain.Value;
import edu.hust.factory.repository.AttributeRepository;
import edu.hust.factory.repository.EmployeeRepository;
import edu.hust.factory.repository.ValueRepository;
import edu.hust.factory.repository.custom.EmployeeCustomRepository;
import edu.hust.factory.service.EmployeeService;
import edu.hust.factory.service.ValueService;
import edu.hust.factory.service.dto.EmployeeDTO;
import edu.hust.factory.service.exception.CustomException;
import edu.hust.factory.service.mapper.DtoMapper;
import edu.hust.factory.service.mapper.EmployeeMapper;
import edu.hust.factory.service.model.PageFilterInput;
import edu.hust.factory.service.model.PageResponse;
import edu.hust.factory.service.util.Constant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link edu.hust.factory.domain.Employee}.
 */
@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final EmployeeRepository employeeRepository;
    private final ValueRepository valueRepository;
    private final AttributeRepository attributeRepository;
    private final EmployeeCustomRepository employeeCustomRepository;
    private final ValueService valueService;
    private final DtoMapper dtoMapper;
    private final EmployeeMapper employeeMapper;

    public EmployeeServiceImpl(
        EmployeeRepository employeeRepository,
        ValueRepository valueRepository,
        AttributeRepository attributeRepository,
        EmployeeCustomRepository employeeCustomRepository,
        ValueService valueService,
        DtoMapper dtoMapper,
        EmployeeMapper employeeMapper
    ) {
        this.employeeRepository = employeeRepository;
        this.valueRepository = valueRepository;
        this.attributeRepository = attributeRepository;
        this.employeeCustomRepository = employeeCustomRepository;
        this.valueService = valueService;
        this.dtoMapper = dtoMapper;
        this.employeeMapper = employeeMapper;
    }

    @Override
    public Employee save(Employee employee) {
        log.debug("Request to save Employee : {}", employee);
        return employeeRepository.save(employee);
    }

    @Override
    public Employee update(Employee employee) {
        log.debug("Request to update Employee : {}", employee);
        return employeeRepository.save(employee);
    }

    @Override
    public Optional<Employee> partialUpdate(Employee employee) {
        log.debug("Request to partially update Employee : {}", employee);

        return employeeRepository
            .findById(employee.getId())
            .map(existingEmployee -> {
                if (employee.getEmployeeCode() != null) {
                    existingEmployee.setEmployeeCode(employee.getEmployeeCode());
                }
                if (employee.getUsername() != null) {
                    existingEmployee.setUsername(employee.getUsername());
                }
                if (employee.getHashedPassword() != null) {
                    existingEmployee.setHashedPassword(employee.getHashedPassword());
                }
                if (employee.getName() != null) {
                    existingEmployee.setName(employee.getName());
                }
                if (employee.getPhone() != null) {
                    existingEmployee.setPhone(employee.getPhone());
                }
                if (employee.getEmail() != null) {
                    existingEmployee.setEmail(employee.getEmail());
                }
                if (employee.getNote() != null) {
                    existingEmployee.setNote(employee.getNote());
                }
                if (employee.getStatus() != null) {
                    existingEmployee.setStatus(employee.getStatus());
                }
                if (employee.getIsActive() != null) {
                    existingEmployee.setIsActive(employee.getIsActive());
                }

                return existingEmployee;
            })
            .map(employeeRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Employee> findAll(Pageable pageable) {
        log.debug("Request to get all Employees");
        return employeeRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Employee> findOne(Long id) {
        log.debug("Request to get Employee : {}", id);
        return employeeRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Employee : {}", id);
        employeeRepository.deleteById(id);
    }

    //Custom service

    @Override
    public PageResponse<List<EmployeeDTO>> search(PageFilterInput<EmployeeDTO> input) {
        Pageable pageable = Pageable.unpaged();
        if (input.getPageSize() != 0) {
            pageable = PageRequest.of(input.getPageNumber(), input.getPageSize());
        }
        Page<Employee> entityPage = employeeCustomRepository.search(input, pageable);
        List<Employee> entities = entityPage.getContent();
        Map<Long, Employee> entityMap = entities.stream().collect(Collectors.toMap(Employee::getId, Function.identity()));
        List<Value> properties = valueRepository.findByEntityTypeAndEntityIdInAndIsActiveTrue(
            Constant.EntityType.EMPLOYEE,
            entityMap.keySet()
        );
        Map<Long, List<Value>> propertiesMap = properties.stream().collect(Collectors.groupingBy(Value::getEntityId));
        List<EmployeeDTO> resultList = new ArrayList<>();
        for (Employee employee : entityPage.getContent()) {
            EmployeeDTO dto = new EmployeeDTO();
            resultList.add((EmployeeDTO) dtoMapper.toDTO(dto, propertiesMap.get(employee.getId())));
            BeanUtils.copyProperties(employee, dto, "isActive");
        }
        return new PageResponse<List<EmployeeDTO>>(entityPage.getTotalElements()).success().data(resultList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EmployeeDTO create(EmployeeDTO dto) throws CustomException {
        if (dto.getEmployeeCode() == null) throw new CustomException(
            HttpStatusCodes.STATUS_CODE_BAD_REQUEST,
            "employee code cannot be empty"
        );
        Optional<Employee> optional = employeeRepository.findByEmployeeCodeAndIsActiveTrue(dto.getEmployeeCode());
        if (optional.isPresent()) throw new CustomException(
            HttpStatusCodes.STATUS_CODE_CONFLICT,
            "duplicate employee code " + dto.getEmployeeCode()
        );
        Employee toSaveEntity = employeeMapper.toEntity(dto);
        Employee savedEntity = employeeRepository.save(toSaveEntity);
        dto.setEmployeeId(savedEntity.getId());
        if (!dto.getPropertiesMap().isEmpty()) {
            List<Value> valueEntities = valueService.createOrUpdateKeyValueEntity(
                savedEntity.getId(),
                dto.getPropertiesMap(),
                Constant.EntityType.EMPLOYEE
            );
            valueRepository.saveAll(valueEntities);
        }
        return dto;
    }
}
