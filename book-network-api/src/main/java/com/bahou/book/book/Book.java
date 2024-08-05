package com.bahou.book.book;

import com.bahou.book.common.BaseEntity;
import com.bahou.book.feedBack.FeedBack;
import com.bahou.book.history.BookTransactionHistory;
import com.bahou.book.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "book")
@Entity
public class Book extends BaseEntity {

    private String title;
    private String authorName;
    private String isbn;
    private String synopsis;
    private String bookCover;
    private boolean archived;
    private boolean shareable;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    @OneToMany(mappedBy = "book")
    private List<FeedBack> feedBacks;
    @OneToMany(mappedBy = "book")
    private List<BookTransactionHistory> histories;



    @Transient
    public double getRate(){
        if (feedBacks ==null || feedBacks.isEmpty()){
            return 0.0;
        }
        var rate = this.feedBacks.stream()
                .mapToDouble(FeedBack::getNote)
                .average()
                .orElse(0.0);
        double roundedRate = Math.round(rate * 10.0) /10.0;
        return roundedRate;
    }

}
