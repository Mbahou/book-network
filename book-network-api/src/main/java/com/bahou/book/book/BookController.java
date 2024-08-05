package com.bahou.book.book;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Tag(name = "book")
@RequestMapping("books")
public class BookController {

    private final BookService service;

    @PostMapping
    public ResponseEntity<Integer> saveBook(
            @Valid @RequestBody BookRequest request,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.save(request, connectedUser));
    }

    @GetMapping("{book-id}")
    public ResponseEntity<BookResponse> bookById(
            @PathVariable(name = "book-id") Integer bookId
    ) {
        return ResponseEntity.ok(service.bookById(bookId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<BookResponse>> findALl(
            @RequestParam(name = "page" ,defaultValue = "0", required = false) int page,
            @RequestParam(name = "size" ,defaultValue = "10", required = false) int size,
            Authentication connectedUser

    ){
        return ResponseEntity.ok(service.findAllBooks(page,size,connectedUser));
    }
    @GetMapping("/owner")
    public ResponseEntity<PageResponse<BookResponse>> findALlBooksByOwner(
            @RequestParam(name = "page" ,defaultValue = "0", required = false) int page,
            @RequestParam(name = "size" ,defaultValue = "10", required = false) int size,
            Authentication connectedUser

    ){
        return ResponseEntity.ok(service.findAllBooksByOwner(page,size,connectedUser));
    }
    @GetMapping("/borrowed")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findALlBorrowedBooks(
            @RequestParam(name = "page" ,defaultValue = "0", required = false) int page,
            @RequestParam(name = "size" ,defaultValue = "10", required = false) int size,
            Authentication connectedUser

    ){
        return ResponseEntity.ok(service.findAllBorrowedBooks(page,size,connectedUser));
    }
    @GetMapping("/retuned")
    public ResponseEntity<PageResponse<BorrowedBookResponse>> findAallReturnedBook(
            @RequestParam(name = "page" ,defaultValue = "0", required = false) int page,
            @RequestParam(name = "size" ,defaultValue = "10", required = false) int size,
            Authentication connectedUser

    ){
        return ResponseEntity.ok(service.findAllReturnedBook(page,size,connectedUser));
    }
    @PatchMapping("/shareable/{book-id}")
    public ResponseEntity<Integer> updateSherableStatus(
            @PathVariable(name = "book-id") Integer bookId,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.updateSherableStatus(bookId,connectedUser));
    }

    @PatchMapping("/archived/{book-id}")
    public ResponseEntity<Integer> updateArchivedStatus(
            @PathVariable(name = "book-id") Integer bookId,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.updateArchivedStatus(bookId,connectedUser));
    }
    @PostMapping("/borrow/{book-id}")
    public ResponseEntity<Integer> borrowBook(
            @PathVariable(name = "book-id") Integer bookId,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.borrowBook(bookId,connectedUser));
    }
    @PatchMapping("/borrow/return/{book-id}")
    public ResponseEntity<Integer> returnBorrowBook(
            @PathVariable(name = "book-id") Integer bookId,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.returnBorrowBook(bookId,connectedUser));
    }
    @PatchMapping("/borrow/return/approve/{book-id}")
    public ResponseEntity<Integer> approveReturnBorrowBook(
            @PathVariable(name = "book-id") Integer bookId,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.approveReturnBorrowBook(bookId,connectedUser));
    }

    @PostMapping(value = "/cover/{book-id}",consumes = "multipart/form-data")
    public ResponseEntity<?> uploadBookCoverpicture(
            @PathVariable("book-id") Integer bookId,
            @Parameter()
            @RequestPart("file") MultipartFile file,
            Authentication connectedUser
            ){
        service.uploadBookCoverPicture(file,connectedUser,bookId);
        return ResponseEntity.accepted().build();
    }


}
