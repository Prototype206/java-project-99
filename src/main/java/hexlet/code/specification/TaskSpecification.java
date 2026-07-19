package hexlet.code.specification;

import hexlet.code.dto.TaskParamsDTO;
import hexlet.code.model.Task;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import jakarta.persistence.criteria.JoinType;

@Component
public class TaskSpecification {

    public Specification<Task> build(TaskParamsDTO params) {
        if (params == null) {
            return (root, query, cb) -> cb.conjunction();
        }

        return Specification.where(withTitleCont(params.getTitleCont()))
                .and(withAssigneeId(params.getAssigneeId()))
                .and(withStatus(params.getStatus()))
                .and(withLabelId(params.getLabelId()));
    }

    private Specification<Task> withTitleCont(String titleCont) {
        return (root, query, cb) -> {
            if (titleCont == null || titleCont.isBlank()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("name")), "%" + titleCont.toLowerCase() + "%");
        };
    }

    private Specification<Task> withAssigneeId(Long assigneeId) {
        return (root, query, cb) -> {
            if (assigneeId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("assignee").get("id"), assigneeId);
        };
    }

    private Specification<Task> withStatus(String statusSlug) {
        return (root, query, cb) -> {
            if (statusSlug == null || statusSlug.isBlank()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("taskStatus").get("slug"), statusSlug);
        };
    }

    private Specification<Task> withLabelId(Long labelId) {
        return (root, query, cb) -> {
            if (labelId == null) {
                return cb.conjunction();
            }
            if (query != null) {
                query.distinct(true);
            }
            return cb.equal(root.join("labels", JoinType.INNER).get("id"), labelId);
        };
    }
}