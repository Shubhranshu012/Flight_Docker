package com.apigateway.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.apigateway.models.EROLE;
import com.apigateway.models.Role;


@Repository
public interface RoleRepository extends MongoRepository<Role, String> {
  Optional<Role> findByName(EROLE name);
}