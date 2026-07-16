package hexlet.code.repository;

import hexlet.code.model.Task;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    @Override
    @EntityGraph(attributePaths = {"taskStatus", "assignee", "labels"})
    List<Task> findAll();

    @Override
    @EntityGraph(attributePaths = {"taskStatus", "assignee", "labels"})
    List<Task> findAll(Specification<Task> spec);
}