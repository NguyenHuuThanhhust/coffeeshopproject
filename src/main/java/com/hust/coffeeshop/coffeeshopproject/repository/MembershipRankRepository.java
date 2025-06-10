package com.hust.coffeeshop.coffeeshopproject.repository;


import com.hust.coffeeshop.coffeeshopproject.entity.MembershipRank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipRankRepository extends JpaRepository<MembershipRank, String> {
}