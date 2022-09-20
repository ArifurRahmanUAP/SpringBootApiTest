package com.arif.testapi.payloads.Response;

import com.arif.testapi.payloads.CategoryDto;
import com.arif.testapi.payloads.UserDTO;
import com.arif.testapi.repositories.UserRepo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class AllUserResponse {

    private int pageNumber;

    private int pageSize;

    private boolean isLastPage;

    private long totalElements;

    private int totalPages;

    private List<UserResponse> users;
}
