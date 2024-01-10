package edu.hust.factory.repository;

import edu.hust.factory.domain.Attribute;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttributeRepository extends JpaRepository<Attribute, Long> {
    Optional<Attribute> findByIdAndIsActiveTrue(Long id);
    List<Attribute> findAllByEntityTypeAndIsActiveTrue(Integer entityType);
    List<Attribute> findAllByEntityTypeAndColumnTypeAndIsActiveTrue(Integer entityType, Integer columnType);

    default Map<String, Attribute> findAllByEntityTypeAndColumnTypeToMap(Integer entityType, Integer columnType) {
        return findAllByEntityTypeAndColumnTypeAndIsActiveTrue(entityType, columnType)
            .stream()
            .collect(Collectors.toMap(Attribute::getColumnName, Function.identity()));
    }

    List<Attribute> findAllByEntityTypeAndColumnNameInAndIsActiveTrue(Integer entityType, Collection<String> keyName);
}
