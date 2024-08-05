package com.bahou.book.feedBack;

import com.bahou.book.book.Book;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class FeedbackMapper {
    public FeedBack toFeedback(FeedbackRequest request) {
        return FeedBack.builder()
                .note(request.note())
                .comment(request.comment())
                .book(Book.builder()
                        .id(request.bookId())
                        .archived(false) // not required and has no impact  :: just to  satisfy lombok
                        .shareable(false)
                        .build())
                .build();
    }

    public FeedbackResponse toFeedbackReponse(FeedBack feedBack, Integer id) {
        return FeedbackResponse.builder()
                .note(feedBack.getNote())
                .comment(feedBack.getComment())
                .ownFeedback(Objects.equals(feedBack.getCreatedBy(),id))
                .build();

    }
}
