package edu.hust.factory.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import edu.hust.factory.domain.*;
import edu.hust.factory.repository.AttributeRepository;
import edu.hust.factory.repository.custom.EmployeeCustomRepository;
import edu.hust.factory.service.dto.EmployeeDTO;
import edu.hust.factory.service.model.PageFilterInput;
import edu.hust.factory.service.util.AccentUtils;
import edu.hust.factory.service.util.Constant;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityManager;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class EmployeeCustomRepositoryImpl implements EmployeeCustomRepository {

    @Autowired
    EntityManager entityManager;

    @Autowired
    AttributeRepository attributeRepository;

    private final Logger log = LoggerFactory.getLogger(EmployeeCustomRepositoryImpl.class);

    @Override
    public Page<Employee> search(PageFilterInput<EmployeeDTO> input, Pageable pageable) {
        EmployeeDTO filter = input.getFilter();
        QEmployee qEmployee = QEmployee.employee;
        QValue qValue = QValue.value;
        QAttribute qAttribute = QAttribute.attribute;

        // query common dynamic properties
        JPAQuery<Long> commonDynamicPropertiesQuery = new JPAQuery<>(entityManager).select(qValue.entityId).from(qValue);
        BooleanBuilder commonDynamicBooleanBuilder = new BooleanBuilder();
        commonDynamicBooleanBuilder.and(qValue.isActive.isTrue());
        if (!StringUtils.isEmpty(input.getCommon())) {
            commonDynamicBooleanBuilder.and(AccentUtils.containsIgnoreAccent(qValue.commonValue, input.getCommon()));
            commonDynamicPropertiesQuery.where(commonDynamicBooleanBuilder);
        }

        // main query
        JPAQuery<Employee> query = new JPAQueryFactory(entityManager)
            .selectFrom(qEmployee)
            .leftJoin(qValue)
            .on(qValue.entityType.eq(Constant.EntityType.EMPLOYEE))
            .on(qEmployee.id.eq(qValue.entityId));

        if (pageable.isPaged()) {
            query.limit(pageable.getPageSize()).offset(pageable.getOffset());
        }

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(qEmployee.isActive.isTrue());

        // Search common
        if (!StringUtils.isEmpty(input.getCommon())) {
            booleanBuilder.and(
                AccentUtils
                    .containsIgnoreAccent(qEmployee.employeeCode, input.getCommon())
                    .or(AccentUtils.containsIgnoreAccent(qEmployee.username, input.getCommon()))
                    .or(AccentUtils.containsIgnoreAccent(qEmployee.name, input.getCommon()))
                    .or(AccentUtils.containsIgnoreAccent(qEmployee.phone, input.getCommon()))
                    .or(AccentUtils.containsIgnoreAccent(qEmployee.email, input.getCommon()))
                    .or(AccentUtils.containsIgnoreAccent(qEmployee.note, input.getCommon()))
                    .or(qValue.id.in(commonDynamicPropertiesQuery))
            );
        }

        // Search static properties
        if (filter.getEmployeeId() != null) {
            booleanBuilder.and(qEmployee.id.eq(filter.getEmployeeId()));
        }
        if (!StringUtils.isEmpty(filter.getEmployeeCode())) {
            booleanBuilder.and(AccentUtils.containsIgnoreAccent(qEmployee.employeeCode, filter.getEmployeeCode()));
        }
        if (!StringUtils.isEmpty(filter.getUsername())) {
            booleanBuilder.and(AccentUtils.containsIgnoreAccent(qEmployee.username, filter.getUsername()));
        }
        if (!StringUtils.isEmpty(filter.getName())) {
            booleanBuilder.and(AccentUtils.containsIgnoreAccent(qEmployee.name, filter.getName()));
        }
        if (!StringUtils.isEmpty(filter.getPhone())) {
            booleanBuilder.and(AccentUtils.containsIgnoreAccent(qEmployee.phone, filter.getPhone()));
        }
        if (!StringUtils.isEmpty(filter.getEmail())) {
            booleanBuilder.and(AccentUtils.containsIgnoreAccent(qEmployee.email, filter.getEmail()));
        }
        if (!StringUtils.isEmpty(filter.getNote())) {
            booleanBuilder.and(AccentUtils.containsIgnoreAccent(qEmployee.note, filter.getNote()));
        }
        if (filter.getStatus() != null) {
            booleanBuilder.and(qEmployee.status.eq(filter.getStatus()));
        }

        // Search dynamic properties
        if (!filter.getPropertiesMap().isEmpty()) {
            Map<String, Attribute> columnEntitiesMap = attributeRepository.findAllByEntityTypeAndColumnTypeToMap(
                Constant.EntityType.EMPLOYEE,
                Constant.ColumnType.DYNAMIC
            );
            for (String columnName : filter.getPropertiesMap().keySet()) {
                JPAQuery<Long> dynamicPropertiesQuery = new JPAQuery<>(entityManager)
                    .select(qValue.entityId)
                    .from(qValue)
                    .join(qAttribute)
                    .on(qValue.columnId.eq(qAttribute.id));
                BooleanBuilder dynamicBooleanBuilder = new BooleanBuilder();
                dynamicBooleanBuilder.and(qValue.isActive.isTrue());
                String value = filter.getPropertiesMap().get(columnName);
                if (!StringUtils.isEmpty(value)) {
                    dynamicBooleanBuilder.and(qAttribute.columnName.eq(columnName));
                    switch (columnEntitiesMap.get(columnName).getDataType()) {
                        case Constant.DataType.INTEGER:
                            dynamicBooleanBuilder.and(
                                AccentUtils.containsIgnoreAccent((StringPath) qValue.integerValue.stringValue(), value)
                            );
                            break;
                        case Constant.DataType.DOUBLE:
                            dynamicBooleanBuilder.and(
                                AccentUtils.containsIgnoreAccent((StringPath) qValue.doubleValue.stringValue(), value)
                            );
                            break;
                        case Constant.DataType.STRING:
                            dynamicBooleanBuilder.and(AccentUtils.containsIgnoreAccent(qValue.stringValue, value));
                            break;
                        case Constant.DataType.TIME: // TODO: take note datetime
                            DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
                            String[] datetime = value.split(" ");
                            Timestamp startTime = Timestamp.valueOf(datetime[0]);
                            Timestamp endTime = Timestamp.valueOf(datetime[1]);
                            dynamicBooleanBuilder.and(qValue.timeValue.between(startTime, endTime));
                            break;
                        case Constant.DataType.BOOLEAN:
                            dynamicBooleanBuilder.and(qValue.booleanValue.stringValue().eq(value));
                            break;
                    }
                    dynamicPropertiesQuery.where(dynamicBooleanBuilder);
                    booleanBuilder.and(qEmployee.id.in(dynamicPropertiesQuery));
                }
            }
        }

        // Sort
        //        if (!StringUtils.isEmpty(input.getSortProperty())) {
        //            List<Attribute> columns =
        //                    attributeRepository.findAllByEntityTypeAndIsActiveTrue(Constant.EntityType.EMPLOYEE);
        //            boolean isSorted = false;
        //            for (Attribute column : columns) {
        //                // Valid sort column name
        //                if (input.getSortProperty().equals(column.getColumnName())) {
        //                    isSorted = true;
        //                    if (Objects.equals(column.getColumnType(), Constant.ColumnType.STATIC) ||
        //                            Objects.equals(column.getColumnType(), Constant.ColumnType.FIXED)) {
        //                        // Default col
        //                        Path<Object> fieldPath = Expressions.path(Object.class, qEmployeeEntity, input.getSortProperty());
        //                        query.orderBy(new OrderSpecifier(input.getSortOrder(), fieldPath));
        //                    } else {
        //                        // Dynamic col
        //                        switch (column.getDataType()) {
        //                            case Constant.DataType.INTEGER:
        //                                query.orderBy(new OrderSpecifier<>(input.getSortOrder(), qValueEntity.integerValue));
        //                                break;
        //                            case Constant.DataType.DOUBLE:
        //                                query.orderBy(new OrderSpecifier<>(input.getSortOrder(), qValueEntity.doubleValue));
        //                                break;
        //                            case Constant.DataType.STRING:
        //                                query.orderBy(new OrderSpecifier<>(input.getSortOrder(), qValueEntity.stringValue));
        //                                break;
        //                            case Constant.DataType.TIME:
        //                                query.orderBy(new OrderSpecifier<>(input.getSortOrder(), qValueEntity.timeValue));
        //                                break;
        //                            case Constant.DataType.BOOLEAN:
        //                                query.orderBy(new OrderSpecifier<>(input.getSortOrder(), qValueEntity.booleanValue));
        //                                break;
        //                        }
        //                    }
        //                }
        //            }
        //            if (!isSorted) {
        //                Path<Object> fieldPath = Expressions.path(Object.class, qEmployeeEntity, input.getSortProperty());
        //                query.orderBy(new OrderSpecifier(input.getSortOrder(), fieldPath));
        //            }
        //        }
        query.where(booleanBuilder).groupBy(qEmployee.id);
        log.debug(query.toString());
        List<Employee> result = query.fetch();
        return new PageImpl<>(result, pageable, query.fetchCount());
    }
}
