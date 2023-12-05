package ru.practicum.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.server.entity.Request;

public interface RequestRepository extends JpaRepository<Request, Long>, QuerydslPredicateExecutor<Request> {
}
