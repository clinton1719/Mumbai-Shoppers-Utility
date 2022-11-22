package com.mshoppers.admin.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.mshoppers.common.entity.Role;

@Repository
public interface RoleRepository extends CrudRepository<Role, Integer>{

}
