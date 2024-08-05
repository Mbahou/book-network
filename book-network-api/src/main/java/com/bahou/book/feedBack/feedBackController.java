package com.bahou.book.feedBack;

import com.bahou.book.book.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.naming.OperationNotSupportedException;

@RestController
@RequiredArgsConstructor
@Tag(name = "Feedback")
@RequestMapping("feedbacks")
public class feedBackController {
    private final FeedbackService service;

    @PostMapping
    public ResponseEntity<Integer> saveFeedback(
            @Valid @RequestBody FeedbackRequest request,
            Authentication connectUser
    ) throws OperationNotSupportedException {
        return ResponseEntity.ok(service.save(request,connectUser));
    }
    @GetMapping("/book/{book-id}")
    public ResponseEntity<PageResponse<FeedbackResponse>> findAllFeedbackByBook(
            @PathVariable("book-id") Integer bookId,
            @RequestParam(name ="page", defaultValue = "0", required = false) int page,
            @RequestParam(name ="size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.findAllFeedbackByBook(bookId,page,size,connectedUser));
    }


}
