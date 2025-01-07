package com.example.voting_system_app.repo;

import com.example.voting_system_app.entity.Option;
import com.example.voting_system_app.entity.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionRepo extends JpaRepository<Option,Integer> {
}
