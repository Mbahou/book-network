package com.bahou.book.book;

import com.bahou.book.exception.OpertionNotPermittedException;
import com.bahou.book.file.FileStorageService;
import com.bahou.book.history.BookTransactionHistory;
import com.bahou.book.history.BookTransactionHistoryRepository;
import com.bahou.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

import static com.bahou.book.book.BookSpecification.withOwnerId;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookMapper bookMapper;
    private final BookRepository bookRepository;
    private final BookTransactionHistoryRepository transactionHistoryRepository;
    private final FileStorageService fileStorageService;
    public Integer save(BookRequest request, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Book book = bookMapper.toBook(request);
        book.setOwner(user);
        return bookRepository.save(book).getId();
    }

    public BookResponse bookById(Integer id) {
        return bookRepository.findById(id)
                .map(bookMapper :: toBookResponse)
                .orElseThrow(()->new EntityNotFoundException("no book found with the ID :: "+id));
    }

    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page,size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable,user.getId());
        List<BookResponse> bookResponse = books.stream()
                .map(bookMapper :: toBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponse,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()

        );
    }

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page,size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAll(withOwnerId(user.getId()),pageable);
        List<BookResponse> bookResponse = books.stream()
                .map(bookMapper :: toBookResponse)
                .toList();
         return new PageResponse<>(
                bookResponse,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()

        );

    }

    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page,size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory>  allBorrowedBooks = transactionHistoryRepository.findAllBorrowedBooks(pageable,user.getId());
        List<BorrowedBookResponse>  historyResponses = allBorrowedBooks
                .stream()
                .map(bookMapper :: toBorrowedBookResponse)
                .toList();
        return  new PageResponse<>(
                historyResponses,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()

        );
    }

    public PageResponse<BorrowedBookResponse> findAllReturnedBook(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page,size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory>  allBorrowedBooks = transactionHistoryRepository.findAllReturnedBooks(pageable,user.getId());
        List<BorrowedBookResponse>  historyResponses = allBorrowedBooks
                .stream()
                .map(bookMapper :: toBorrowedBookResponse)
                .toList();
        return  new PageResponse<>(
                historyResponses,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()

        );

    }

    public Integer updateSherableStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()->new EntityNotFoundException("No book found with the ID :: "+bookId));
        User user = ((User) connectedUser.getPrincipal());
        if (!Objects.equals(book.getOwner().getId(),user.getId())){
            // throw an exception
            throw new OpertionNotPermittedException("you  cannot update others books shareable status");
        }
        book.setShareable(!book.isShareable());
        bookRepository.save(book);
        return bookId;
    }

    public Integer updateArchivedStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()->new EntityNotFoundException("No book found with the ID :: "+bookId));
        User user = ((User) connectedUser.getPrincipal());
        if (!Objects.equals(book.getOwner().getId(),user.getId())){
            // throw an exception
            throw new OpertionNotPermittedException("you  cannot update others books archived status");
        }
        book.setArchived(!book.isArchived());
        bookRepository.save(book);
        return bookId;

    }

    public Integer borrowBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId) //1
                .orElseThrow(()->new EntityNotFoundException("No book found with the ID :: "+bookId));
         // archived and not shareable
        if (book.isArchived() || !book.isShareable()){
            throw  new  OpertionNotPermittedException("the request book cannot  be borrowed since it is archived or not shareable ");
        }
        User user = ((User) connectedUser.getPrincipal());
        if (Objects.equals(book.getOwner().getId(),user.getId())){
            // throw an exception
            throw new OpertionNotPermittedException("you  cannot borrow your own  book ");
        }
        boolean isAlReadyBrrowed = transactionHistoryRepository.isAlreadyBorrowedByUser(bookId,user.getId());
        if (isAlReadyBrrowed){
            throw  new  OpertionNotPermittedException("the requested book id already  borrowed ");
        }
        BookTransactionHistory  bookTransactionHistory  = BookTransactionHistory.builder()
                .user(user)
                .book(book)
                .returnApproved(false)
                .returned(false)
                .build();
        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Integer returnBorrowBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()->new EntityNotFoundException("No book found with the ID :: "+bookId));
        // archived and not shareable
        if (book.isArchived() || !book.isShareable()){
            throw  new  OpertionNotPermittedException("the request book cannot  be borrowed since it is archived or not shareable ");
        }
        User user = ((User) connectedUser.getPrincipal());
        if (Objects.equals(book.getOwner().getId(),user.getId())){
            // throw an exception
            throw new OpertionNotPermittedException("you  cannot borrow or return  your own  book ");
        }
        BookTransactionHistory bookTransactionHistory = transactionHistoryRepository.findByBookIdAndUserId(bookId,user.getId())
                .orElseThrow(()->new OpertionNotPermittedException("You did not not borrow this book"));
        bookTransactionHistory.setReturned(true);
        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Integer approveReturnBorrowBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()->new EntityNotFoundException("No book found with the ID :: "+bookId));
        // archived and not shareable
        if (book.isArchived() || !book.isShareable()){
            throw  new  OpertionNotPermittedException("the request book cannot  be borrowed since it is archived or not shareable ");
        }
        User user = ((User) connectedUser.getPrincipal());
        if (!Objects.equals(book.getOwner().getId(),user.getId())){
            // throw an exception
            throw new OpertionNotPermittedException("you  cannot return a book that you do not own  ");
        }
        BookTransactionHistory bookTransactionHistory = transactionHistoryRepository.findByBookIdAndOwnerId(bookId,user.getId())
                .orElseThrow(()->new OpertionNotPermittedException("The book is not returned yet. You cannot approve its return "));
        bookTransactionHistory.setReturnApproved(true);
        return transactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public void uploadBookCoverPicture(MultipartFile file, Authentication connectedUser, Integer bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(()->new EntityNotFoundException("No book found with the ID :: "+bookId));
        User user = ((User) connectedUser.getPrincipal());
        var bookCover = fileStorageService.saveFile(file,user.getId());
        book.setBookCover(bookCover);
        bookRepository.save(book);




    }
}
