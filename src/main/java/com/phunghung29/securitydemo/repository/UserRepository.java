package com.phunghung29.securitydemo.repository;

import com.phunghung29.securitydemo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    User findByEmail(String email);

    @Query("SELECT u FROM User u WHERE LOWER(u.email) like CONCAT('%',:email,'%')")
    List<User> findBySearchEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE LOWER(u.gender) like CONCAT('',:gender,'')")
    List<User> findBySearchGender(@Param("gender") String gender);

    @Query("SELECT u FROM User u WHERE u.age = :age")
    List<User> findBySearchAge(@Param("age") Integer age);

}
