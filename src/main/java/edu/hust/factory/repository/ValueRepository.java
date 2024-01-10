package edu.hust.factory.repository;

import edu.hust.factory.domain.Value;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValueRepository extends JpaRepository<Value, Long> {
    List<Value> findByEntityTypeAndEntityIdInAndIsActiveTrue(Integer entityType, Collection<Long> entityIds);
    Optional<Value> findByEntityIdAndColumnIdAndIsActiveTrue(Long entityId, Long columnId);
}
