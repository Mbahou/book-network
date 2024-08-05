package com.bahou.book.feedBack;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FeedbackRepository extends JpaRepository<FeedBack,Integer> {
    @Query("""
           select feedback 
           from FeedBack feedback
           where feedback.book.id=:bookId
           """)
    Page<FeedBack> findAllByBookId(Integer bookId, Pageable pageable);
}
