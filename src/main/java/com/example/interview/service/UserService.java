        //
        // Source code recreated from a .class file by IntelliJ IDEA
        // (powered by Fernflower decompiler)
        //

        package com.example.interview.service;

        import com.example.interview.constaint.CacheName;
        import com.example.interview.entity.caching.QUser;
        import com.example.interview.entity.caching.User;
        import com.example.interview.repository.CacheRepository.UserRepository;
        import jakarta.persistence.Cacheable;
        import jakarta.transaction.Transactional;

        import java.util.List;
        import java.util.UUID;

        import lombok.AllArgsConstructor;
        import lombok.Generated;
        import org.springframework.security.oauth2.core.oidc.user.OidcUser;
        import org.springframework.stereotype.Service;
        @AllArgsConstructor
        @Service
        public class UserService {
            private final UserRepository userRepository;

            @Transactional
            public User SyncUser(OidcUser oidcUser) {
                String email = oidcUser.getEmail();
                return userRepository.findByEmail(email).orElseGet(() -> {
                    User newUser = new User();
                    newUser.setId(java.util.UUID.randomUUID());
                    newUser.setEmail(email);
                    newUser.setFullName(oidcUser.getName());
                    newUser.setActive(true);
                    newUser.setDeleted(false);
                    newUser.setCognitoId(oidcUser.getSubject());
                    // You can set createdBy and updatedBy if you have that information
                    return userRepository.save(newUser);
                });
            }
            public User getUserById(UUID userId) {
                QUser qUser = QUser.user;
               return userRepository.findOne(qUser.id.eq(userId)).orElse(null);
            }

        }
