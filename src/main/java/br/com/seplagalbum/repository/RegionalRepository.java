package br.com.seplagalbum.repository;

import br.com.seplagalbum.model.Regional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegionalRepository extends JpaRepository<Regional, Long> {
    Optional<Regional> findByIdAndAtivoTrue(Integer id);
    java.util.List<Regional> findByAtivoTrue();
}
