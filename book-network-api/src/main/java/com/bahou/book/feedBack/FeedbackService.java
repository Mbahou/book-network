package com.bahou.book.feedBack;

import com.bahou.book.book.Book;
import com.bahou.book.book.BookRepository;
import com.bahou.book.book.PageResponse;
import com.bahou.book.exception.OpertionNotPermittedException;
import com.bahou.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.naming.OperationNotSupportedException;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackService {
    private final BookRepository bookRepository;
    private final FeedbackMapper feedbackMapper;
    private final FeedbackRepository repository;
    public Integer save(FeedbackRequest request, Authentication connectUser) throws OperationNotSupportedException {
        Book book =  bookRepository.findById(request.bookId())
                .orElseThrow(()->new EntityNotFoundException("No book found with the ID :: " + request.bookId()));
        if (book.isArchived() || !book.isShareable()){
            throw  new OpertionNotPermittedException("You cannot give a feedback for an archived or not shareable book");
        }
        User user = ((User) connectUser.getPrincipal());
        if (Objects.equals(book.getOwner().getId(),user.getId())){
            throw new OperationNotSupportedException("You cannot give a feedback to your own book");
        }
        FeedBack feedBack = feedbackMapper.toFeedback(request);
        return repository.save(feedBack).getId();
    }

    public PageResponse<FeedbackResponse> findAllFeedbackByBook(Integer bookId, int page, int size, Authentication connectedUser) {
        Pageable pageable = PageRequest.of(page,size);
        User user = ((User) connectedUser.getPrincipal());
        Page<FeedBack>  feedBacks = repository.findAllByBookId(bookId,pageable);
        List<FeedbackResponse> feedbackResponses = feedBacks.stream()
                .map(f->feedbackMapper.toFeedbackReponse(f,user.getId()))
                .toList();
        return new PageResponse<>(
                feedbackResponses,
                feedBacks.getNumber(),
                feedBacks.getSize(),
                feedBacks.getTotalElements(),
                feedBacks.getTotalPages(),
                feedBacks.isFirst(),
                feedBacks.isLast()
        );
    }
}
