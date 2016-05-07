package com.dornalame.app.repository;

import com.dornalame.app.domain.ForMessages;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the ForMessages entity.
 */
@SuppressWarnings("unused")
public interface ForMessagesRepository extends JpaRepository<ForMessages,Long> {

}
