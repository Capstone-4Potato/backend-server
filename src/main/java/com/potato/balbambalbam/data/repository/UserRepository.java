package com.potato.balbambalbam.data.repository;

import com.potato.balbambalbam.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
<<<<<<< HEAD
    User findBySocialId(String socialId);

    Boolean existsBySocialId(String SocialId);

    User findByName(String name);

=======
    @Override
    Optional<User> findById(Long aLong);

>>>>>>> 4ef86e6f9a24d200830a208d7ce76b58bdd45a22
}
