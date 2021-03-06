package com.example.taks_2229_2021_user.controller;

import com.example.taks_2229_2021_user.exception.MailException;
import com.example.taks_2229_2021_user.exception.UsernameException;
import com.example.taks_2229_2021_user.exception.UsernameExitException;
import com.example.taks_2229_2021_user.model.Users;
import com.example.taks_2229_2021_user.model.utils.PagingHeaders;
import com.example.taks_2229_2021_user.model.utils.PagingResponse;
import com.example.taks_2229_2021_user.payload.UserDto;
import com.example.taks_2229_2021_user.payload.UserResponse;
import com.example.taks_2229_2021_user.service.ClientService;
import com.example.taks_2229_2021_user.service.UsersService;
import com.example.taks_2229_2021_user.service.impl.UserSearch;
import com.example.taks_2229_2021_user.util.AppConstants;
import com.fasterxml.jackson.core.JsonProcessingException;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/user")
@PreAuthorize("isAuthenticated()")
public class UserController {
    @Autowired
    private UsersService usersService;
    @Autowired
    private UserSearch userSearch;
    @Autowired
    private ClientService clientService;

    @GetMapping("/login")
    public ResponseEntity<Boolean> login() {
        return ResponseEntity.ok(true);
    }

//    @PostMapping("/page")
//    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
//        return new ResponseEntity<>(usersService.createUserMap(userDto), HttpStatus.CREATED);
//    }

    @GetMapping("/checkemail/{email}")
    public ResponseEntity<Users> findUsersByEmail(@PathVariable String email) {
        return new ResponseEntity<Users>(usersService.checkMailForgotPass(email), HttpStatus.OK);
    }

    @PostMapping("/changePassForgot")
    public ResponseEntity<Boolean> changePassUserThenForgotPas(@RequestBody Users users) {
        return new ResponseEntity<>(usersService.changePassUserThenForgotPas(users), HttpStatus.OK);
    }

    //maybe delete


    @GetMapping("/page")
    public UserResponse getAllUsers(@RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo, @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize, @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy, @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir) {
        return usersService.getAllUsersPage(pageNo, pageSize, sortBy, sortDir);
    }

    @GetMapping("user-not-pagination")
    public ResponseEntity<Iterable<Users>> getAllUser() {
        usersService.getAllUsers();
        return new ResponseEntity<>(usersService.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/finduser/{username}")
    public ResponseEntity<Users> getUserByUsername(@PathVariable("username") String name) throws UsernameException {
        return new ResponseEntity<>(usersService.getUserByUserName(name), HttpStatus.OK);
    }

    @GetMapping("/check/{username}/{password}")
    public ResponseEntity<Boolean> checkPassword(@PathVariable("username") String name, @PathVariable("password") String password) throws UsernameException {
        return new ResponseEntity<Boolean>(usersService.checkPassword(password, name), HttpStatus.OK);
    }

    @PostMapping("signup")
    public ResponseEntity<Users> signup(@RequestBody @Valid Users users) throws UsernameExitException, MailException {
        return new ResponseEntity<>(usersService.signupUser(users), HttpStatus.OK);
    }

    @PostMapping("create")
    public ResponseEntity<Users> createUser(@RequestBody @Valid Users users) throws UsernameExitException, MailException {
        return new ResponseEntity<>(usersService.createUser(users), HttpStatus.OK);
    }

    @PutMapping("/update/{username}")
    public ResponseEntity<Users> updateUser(@PathVariable("username") String name, @RequestBody @Valid Users users) throws UsernameException, UsernameExitException, MailException {
        return new ResponseEntity<>(usersService.updateUser(name, users), HttpStatus.OK);
    }

    @PutMapping("/change/{username}")
    public ResponseEntity<Boolean> changePassword(@PathVariable("username") String name, @RequestBody UserDto userDto) throws UsernameException, JsonProcessingException {
        return new ResponseEntity<>(usersService.changePassword(userDto.getPassword(), name), HttpStatus.OK);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Users> deleteUser(@PathVariable("username") String name) throws UsernameException {
        return new ResponseEntity<>(usersService.deleteUser(name), HttpStatus.OK);
    }

    @Transactional
    @GetMapping(value = "/mapsearch", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Users>> get(@And({@Spec(path = "username", params = "username", spec = Like.class), @Spec(path = "firstname", params = "firstname", spec = Like.class), @Spec(path = "lastname", params = "lastname", spec = Like.class), @Spec(path = "email", params = "email", spec = Like.class)}) Specification<Users> spec, Sort sort, @RequestHeader HttpHeaders headers) {
        final PagingResponse response = userSearch.get(spec, headers, sort);
        return new ResponseEntity<>(response.getElements(), returnHttpHeaders(response), HttpStatus.OK);
    }

    public HttpHeaders returnHttpHeaders(PagingResponse response) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(PagingHeaders.COUNT.getName(), String.valueOf(response.getCount()));
        headers.set(PagingHeaders.PAGE_SIZE.getName(), String.valueOf(response.getPageSize()));
        headers.set(PagingHeaders.PAGE_OFFSET.getName(), String.valueOf(response.getPageOffset()));
        headers.set(PagingHeaders.PAGE_NUMBER.getName(), String.valueOf(response.getPageNumber()));
        headers.set(PagingHeaders.PAGE_TOTAL.getName(), String.valueOf(response.getPageTotal()));
        return headers;
    }


}
