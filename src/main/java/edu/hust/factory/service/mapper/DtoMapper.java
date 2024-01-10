package edu.hust.factory.service.mapper;

import edu.hust.factory.domain.Attribute;
import edu.hust.factory.domain.Value;
import edu.hust.factory.repository.AttributeRepository;
import edu.hust.factory.service.dto.BaseDynamicDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class DtoMapper {

    @Autowired
    AttributeRepository attributeRepository;

    public BaseDynamicDTO toDTO(BaseDynamicDTO unmappedDTO, List<Value> properties) {
        if (CollectionUtils.isEmpty(properties)) return unmappedDTO;
        for (Value property : properties) {
            Optional<Attribute> attrOptional = attributeRepository.findByIdAndIsActiveTrue(property.getColumnId());
            if (attrOptional.isPresent()) {
                String columnName = attrOptional.get().getColumnName();
                if (property.getBooleanValue() != null) {
                    unmappedDTO.getPropertiesMap().put(columnName, String.valueOf(property.getBooleanValue()));
                }
                if (property.getIntegerValue() != null) {
                    unmappedDTO.getPropertiesMap().put(columnName, String.valueOf(property.getIntegerValue()));
                }
                if (property.getDoubleValue() != null) {
                    unmappedDTO.getPropertiesMap().put(columnName, String.valueOf(property.getDoubleValue()));
                }

                if (property.getStringValue() != null) {
                    unmappedDTO.getPropertiesMap().put(columnName, String.valueOf(property.getStringValue()));
                }

                if (property.getTimeValue() != null) {
                    unmappedDTO.getPropertiesMap().put(columnName, String.valueOf(property.getTimeValue()));
                }
            }
        }
        return unmappedDTO;
    }
}
