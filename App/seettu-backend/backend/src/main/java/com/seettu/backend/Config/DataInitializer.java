package com.seettu.backend.Config;

import com.seettu.backend.entity.Role;
import com.seettu.backend.entity.User;
import com.seettu.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        fixDatabaseConstraint();
        createDefaultAdminIfNotExists();
    }
    
    private void fixDatabaseConstraint() {
        try {
            System.out.println("🔧 Fixing database constraint to allow ADMIN role...");
            
            // Drop existing constraint
            jdbcTemplate.execute("ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check");
            
            // Add new constraint with ADMIN role
            jdbcTemplate.execute("ALTER TABLE users ADD CONSTRAINT users_role_check CHECK (role IN ('PROVIDER', 'SUBSCRIBER', 'ADMIN'))");
            
            System.out.println("✅ Database constraint updated successfully!");
            
        } catch (Exception e) {
            System.out.println("❌ Error updating database constraint: " + e.getMessage());
            System.out.println("📝 Please run this SQL manually:");
            System.out.println("   ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;");
            System.out.println("   ALTER TABLE users ADD CONSTRAINT users_role_check CHECK (role IN ('PROVIDER', 'SUBSCRIBER', 'ADMIN'));");
        }
    }

    private void createDefaultAdminIfNotExists() {
        try {
            // Check if any admin exists
            if (userRepository.countByRole(Role.ADMIN) == 0) {
                User admin = new User();
                admin.setName("System Administrator");
                admin.setEmail("admin@seettu.com");
                admin.setPassword(passwordEncoder.encode("Admin@123"));
                admin.setRole(Role.ADMIN);
                admin.setCreatedDate(LocalDateTime.now());
                
                userRepository.save(admin);
                
                System.out.println("=========================================");
                System.out.println("🚀 DEFAULT ADMIN CREATED SUCCESSFULLY!");
                System.out.println("📧 Email: admin@seettu.com");
                System.out.println("🔑 Password: Admin@123");
                System.out.println("⚠️  PLEASE CHANGE PASSWORD AFTER LOGIN!");
                System.out.println("=========================================");
            } else {
                System.out.println("✅ Admin user already exists in the system.");
            }
        } catch (Exception e) {
            System.out.println("❌ Error creating admin user: " + e.getMessage());
            System.out.println("💡 Please check database constraints and try again.");
        }
    }
}
