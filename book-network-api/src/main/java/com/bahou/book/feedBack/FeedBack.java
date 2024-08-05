package com.bahou.book.feedBack;

import com.bahou.book.book.Book;
import com.bahou.book.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class FeedBack  extends BaseEntity {

    private Double note;
    private String comment;
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;


}
