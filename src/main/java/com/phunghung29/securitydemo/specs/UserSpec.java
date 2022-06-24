package com.phunghung29.securitydemo.specs;

import com.phunghung29.securitydemo.entity.User;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class UserSpec {
    public static Specification<User> filter(Integer age, String gender, Boolean isActivated) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            // = age
            if (age != null) {
                Predicate pre = criteriaBuilder.equal(root.get("age"), age);
                predicateList.add(pre);
            }
            if (gender != null) {
//                Predicate pre = criteriaBuilder.equal(root.get("gender"), gender);
                Predicate pre= criteriaBuilder.like(criteriaBuilder.lower(root.get("gender")), gender);
                predicateList.add(pre);
            }
            if (isActivated != null) {
                Predicate pre = criteriaBuilder.equal(root.get("isActivated"), isActivated);
                predicateList.add(pre);
            }

            return criteriaBuilder.and(predicateList.toArray(new Predicate[0]));
        };
    }
}
