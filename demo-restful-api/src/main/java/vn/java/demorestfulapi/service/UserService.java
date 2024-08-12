package vn.java.demorestfulapi.service;

import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import vn.java.demorestfulapi.dto.request.UserRequestDTO;
import vn.java.demorestfulapi.dto.response.PageResponse;
import vn.java.demorestfulapi.dto.response.UserDetailResponse;
import vn.java.demorestfulapi.model.User;
import vn.java.demorestfulapi.util.UserStatus;

import java.util.List;

public interface UserService {
    User getByUsername(String userName);
//    List<String> findAllRolesByUserId(long userId);

    long saveUser(UserRequestDTO request);

    void updateUser(long userId, UserRequestDTO request);

    void changeStatus(long userId, UserStatus status);

    void deleteUser(long userId);

    UserDetailsService userDetailsService();

    UserDetailResponse getUser(long userId);

    PageResponse<?> getAllUsersWithSortBy(int pageNo, int pageSize, String sortBy);

    PageResponse<?> getAllUsersWithSortByMultipleColumns(int pageNo, int pageSize, String... sorts);

    PageResponse<?> getAllUsersAndSearchWithPagingAndSorting(int pageNo, int pageSize, String search, String sortBy);

    PageResponse<?> advanceSearchWithCriteria(int pageNo, int pageSize, String sortBy, String address, String... search);

    PageResponse<?> advanceSearchWithSpecifications(Pageable pageable, String[] user, String[] address);
}