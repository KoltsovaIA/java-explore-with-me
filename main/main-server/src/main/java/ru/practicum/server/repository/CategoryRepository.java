package ru.practicum.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.server.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
