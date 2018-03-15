package com.cjimgarten.login;

import org.springframework.data.repository.CrudRepository;

/**
 * Created by chris on 3/14/18.
 */
public interface UserRepository extends CrudRepository<User, Long> {}
