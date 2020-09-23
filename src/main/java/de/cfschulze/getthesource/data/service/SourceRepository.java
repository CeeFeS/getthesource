package de.cfschulze.getthesource.data.service;

import de.cfschulze.getthesource.data.entity.Source;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SourceRepository extends JpaRepository<Source, Integer> {

}