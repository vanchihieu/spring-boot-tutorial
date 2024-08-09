package vn.java.demorestfulapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import vn.java.demorestfulapi.model.Role;
import vn.java.demorestfulapi.model.User;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{

}