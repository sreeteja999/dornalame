package com.dornalame.app.repository;

import com.dornalame.app.domain.Reference;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Reference entity.
 */
@SuppressWarnings("unused")
public interface ReferenceRepository extends JpaRepository<Reference,Long> {

}
