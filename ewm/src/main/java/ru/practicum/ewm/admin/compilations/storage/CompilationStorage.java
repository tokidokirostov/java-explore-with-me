package ru.practicum.ewm.admin.compilations.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.admin.compilations.model.Compilation;

public interface CompilationStorage extends JpaRepository<Compilation, Long> {

    @Query
            ("SELECT c from Compilation as c " +
                    "where ((:pinned) is null or c.pinned in :pinned) ")
    Page<Compilation> searchAll(Boolean pinned, Pageable page);
}
