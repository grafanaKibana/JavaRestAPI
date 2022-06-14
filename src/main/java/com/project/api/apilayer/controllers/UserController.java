package com.project.api.apilayer.controllers;

import com.project.api.apilayer.models.UserCreateModel;
import com.project.api.apilayer.models.UserResponseModel;
import com.project.api.apilayer.models.UserUpdateModel;
import com.project.api.businesslogiclayer.mappers.UserMapper;
import com.project.api.businesslogiclayer.services.interfaces.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final IUserService userService;
    private final UserMapper mapper;

    public UserController(IUserService userService, UserMapper mapper){
        this.userService = userService;
        this.mapper = mapper;
    }

    @GetMapping
    public Iterable<UserResponseModel> GetUsers()
    {
        return userService.GetUsers();
    }

    @GetMapping("/{userId}")
    public UserResponseModel GetUser(@PathVariable int userId)
    {
        return userService.GetUser(userId);
    }

    @PostMapping
    public ResponseEntity<Void> CreateUser(@RequestBody UserCreateModel userModel)
    {
        userService.CreateUser(userModel);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Void> UpdateUser(@PathVariable int userId, @RequestBody UserUpdateModel userModel)
    {
        userService.UpdateUser(userId, userModel);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> DeleteUser(@PathVariable int userId)
    {
        userService.DeleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}