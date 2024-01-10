package edu.hust.factory.service;

import edu.hust.factory.domain.Value;
import edu.hust.factory.service.exception.CustomException;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public interface ValueService {
    List<Value> createOrUpdateKeyValueEntity(Long entityId, Map<String, String> inputMap, int entityType) throws CustomException;
}
