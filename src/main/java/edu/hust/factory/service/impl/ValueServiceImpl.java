package edu.hust.factory.service.impl;

import com.google.api.client.http.HttpStatusCodes;
import edu.hust.factory.domain.Attribute;
import edu.hust.factory.domain.Value;
import edu.hust.factory.repository.AttributeRepository;
import edu.hust.factory.repository.ValueRepository;
import edu.hust.factory.service.ValueService;
import edu.hust.factory.service.exception.CustomException;
import edu.hust.factory.service.util.Constant;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ValueServiceImpl implements ValueService {

    private static final Logger log = LoggerFactory.getLogger(ValueService.class);

    @Autowired
    AttributeRepository attributeRepository;

    @Autowired
    ValueRepository valueRepository;

    @Override
    public List<Value> createOrUpdateKeyValueEntity(Long entityId, Map<String, String> inputMap, int entityType) throws CustomException {
        List<Attribute> columnPropertyEntities = attributeRepository.findAllByEntityTypeAndColumnNameInAndIsActiveTrue(
            entityType,
            inputMap.keySet()
        );
        Map<String, Attribute> keyNameColumnMap = columnPropertyEntities
            .stream()
            .collect(Collectors.toMap(Attribute::getColumnName, Function.identity()));
        List<Value> keyValueEntities = new ArrayList<>();
        for (String keyName : inputMap.keySet()) {
            Optional<Value> optionalValue = valueRepository.findByEntityIdAndColumnIdAndIsActiveTrue(
                entityId,
                keyNameColumnMap.get(keyName).getId()
            );
            Value keyValueEntity = new Value();
            if (optionalValue.isEmpty()) {
                keyValueEntity = new Value();
                keyValueEntity.setEntityType(entityType);
                keyValueEntity.setEntityId(entityId);
                keyValueEntity.setColumnId(keyNameColumnMap.get(keyName).getId());
            }

            int dataType = keyNameColumnMap.get(keyName).getDataType();
            try {
                if ((inputMap.get(keyName) == null)) {
                    keyValueEntity.setCommonValue(null);
                } else {
                    keyValueEntity.setCommonValue(inputMap.get(keyName));
                }
                if (dataType == Constant.DataType.INTEGER) {
                    keyValueEntity.setIntegerValue(Long.parseLong(inputMap.get(keyName)));
                } else if (dataType == Constant.DataType.DOUBLE) {
                    keyValueEntity.setDoubleValue(Double.parseDouble(inputMap.get(keyName)));
                } else if (dataType == Constant.DataType.STRING) {
                    keyValueEntity.setStringValue(inputMap.get(keyName));
                } else if (dataType == Constant.DataType.TIME) {
                    keyValueEntity.setTimeValue(Timestamp.valueOf(inputMap.get(keyName)));
                }
            } catch (Exception e) {
                log.error("Invalid data type", e);
                throw new CustomException(
                    HttpStatusCodes.STATUS_CODE_BAD_REQUEST,
                    "invalid datatype " + keyNameColumnMap.get(keyName).getColumnName()
                );
            }

            keyValueEntities.add(keyValueEntity);
        }
        return keyValueEntities;
    }
}
