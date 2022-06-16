package com.project.api.businesslogiclayer.services;

import com.project.api.businesslogiclayer.exceptions.BadRequestException;
import com.project.api.businesslogiclayer.exceptions.ResourceNotFoundException;
import com.project.api.businesslogiclayer.common.PasswordHelper;
import com.project.api.businesslogiclayer.exceptions.UnauthorizedException;
import com.project.api.businesslogiclayer.mappers.UserMapper;
import com.project.api.apilayer.models.UserCreateModel;
import com.project.api.apilayer.models.UserResponseModel;
import com.project.api.apilayer.models.UserUpdateModel;
import com.project.api.businesslogiclayer.services.interfaces.IUserService;
import com.project.api.dataaccesslayer.entities.User;
import com.project.api.dataaccesslayer.repositories.UserRepository;
import de.mobiuscode.nameof.Name;
import javassist.tools.web.BadHttpRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class UserService implements IUserService
{
    private final UserRepository repository;
    private final UserMapper mapper;

    public UserService(UserRepository userRepository, UserMapper mapper)
    {
        this.repository = userRepository;
        this.mapper = mapper;
    }

    public List<UserResponseModel> GetUsers()
    {
        return repository.findAll()
                .stream()
                .map(mapper::MapToResponseModel)
                .collect(Collectors.toList());
    }

    public UserResponseModel GetUser(long userId)
    {
        var userToFind = repository.findById(userId).orElseThrow(()
                -> new ResourceNotFoundException(User.class.getSimpleName(), Name.of(User.class, User::getId), userId));
        return mapper.MapToResponseModel(userToFind);
    }

    public void CreateUser(UserCreateModel userModel)
    {
        var userToCreate = mapper.MapToEntity(userModel);
        userToCreate.setNormalizedEmail(userModel.getEmail().toUpperCase(Locale.ROOT));
        userToCreate.setNormalizedUserName(userModel.getUsername().toUpperCase(Locale.ROOT));
        userToCreate.setRegistrationTime(Instant.now());
        userToCreate.setPasswordHash(PasswordHelper.HashPassword(userModel.getPassword()));
        repository.save(userToCreate);
    }

    public void UpdateUser(long userId, UserUpdateModel userModel)
    {
        var user = repository.findById(userId).orElseThrow(()
                -> new ResourceNotFoundException(User.class.getSimpleName(), Name.of(User.class, User::getId), userId));

            user.setFirstName(userModel.getFirstName());
            user.setLastName(userModel.getLastName());
            user.setUsername(userModel.getUsername());

        repository.save(user);
    }


    public void UpdateUserPassword(long userId, String oldPassword, String newPassword, String newPasswordCheck) {
        if (newPassword.equals(newPasswordCheck)){
            var user = repository.findById(userId).orElseThrow(()
                    -> new ResourceNotFoundException(User.class.getSimpleName(), Name.of(User.class, User::getId), userId));
            if (PasswordHelper.VerifyPassword(oldPassword, user.getPasswordHash())){
                user.setPasswordHash(PasswordHelper.HashPassword(newPassword));
            }
            else {
                throw new UnauthorizedException();
            }
        }
        else {
            throw new BadRequestException("New password in both fields should be equal");
        }
    }

    public void DeleteUser(long userId)
    {
        repository.deleteById(userId);
    }
}
