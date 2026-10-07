package uz.ustozuz.backend.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import uz.ustozuz.backend.category.Category;
import uz.ustozuz.backend.category.CategoryRepository;
import uz.ustozuz.backend.common.util.SlugUtil;
import uz.ustozuz.backend.course.Course;
import uz.ustozuz.backend.course.CourseRepository;
import uz.ustozuz.backend.course.CourseStatus;
import uz.ustozuz.backend.user.Role;
import uz.ustozuz.backend.user.User;
import uz.ustozuz.backend.user.UserRepository;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppProperties appProperties;

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            return; // Ma'lumotlar allaqachon bor, qayta yozmaymiz
        }

        AppProperties.Seed seed = appProperties.getSeed();
        if (isBlank(seed.getAdminPassword()) || isBlank(seed.getInstructorPassword())) {
            throw new IllegalStateException(
                    "Bo'sh bazani to'ldirish uchun ADMIN_PASSWORD va INSTRUCTOR_PASSWORD o'rnatilishi kerak");
        }

        Map<String, Category> categories = createCategories();
        Map<String, User> instructors = createInstructors();
        createAdmin();
        createCourses(categories, instructors);
    }

    private Map<String, Category> createCategories() {
        record CategoryData(String title, String description, String icon) {}

        CategoryData[] data = {
                new CategoryData("Dasturlash", "Veb, mobil va sun'iy intellekt", "Code2"),
                new CategoryData("Dizayn", "UI/UX, grafik dizayn", "Palette"),
                new CategoryData("Biznes", "Marketing, moliya, boshqaruv", "Briefcase"),
                new CategoryData("Tillar", "Ingliz, rus va boshqa tillar", "Languages"),
                new CategoryData("Fotografiya", "Kamera, tahrirlash, kompozitsiya", "Camera"),
                new CategoryData("Moliya", "Investitsiya, buxgalteriya hisobi", "LineChart"),
                new CategoryData("Sog'liq", "Fitnes, ozuqalanish, salomatlik", "HeartPulse"),
                new CategoryData("Musiqa", "Gitara, pianino, vokal", "Music2"),
        };

        Map<String, Category> result = new HashMap<>();
        for (CategoryData d : data) {
            Category category = new Category();
            category.setTitle(d.title());
            category.setDescription(d.description());
            category.setIcon(d.icon());
            categoryRepository.save(category);
            result.put(d.title(), category);
        }
        return result;
    }

    private Map<String, User> createInstructors() {
        String[] names = {
                "Aziz Karimov", "Nilufar Yusupova", "Jasur Rahimov", "Dilnoza Saidova",
                "Sardor Tojiyev", "Malika Nazarova", "Bekzod Umarov", "Zarina Qodirova",
                "Otabek Yo'ldoshev", "Kamola Rashidova"
        };

        Map<String, User> result = new HashMap<>();
        for (String name : names) {
            User instructor = new User();
            instructor.setName(name);
            instructor.setEmail(SlugUtil.slugify(name).replace("-", ".") + "@ustozuz.uz");
            instructor.setPasswordHash(passwordEncoder.encode(appProperties.getSeed().getInstructorPassword()));
            instructor.setRole(Role.INSTRUCTOR);
            userRepository.save(instructor);
            result.put(name, instructor);
        }
        return result;
    }

    private void createAdmin() {
        User admin = new User();
        admin.setName("Admin");
        admin.setEmail("admin@ustozuz.uz");
        admin.setPasswordHash(passwordEncoder.encode(appProperties.getSeed().getAdminPassword()));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private void createCourses(Map<String, Category> categories, Map<String, User> instructors) {
        record CourseData(
                String title, String instructor, String category,
                double rating, int students, long price, String image
        ) {}

        CourseData[] data = {
                new CourseData("Frontend dasturlash: noldan mutaxassisgacha", "Aziz Karimov", "Dasturlash",
                        4.8, 1240, 249000, "https://images.unsplash.com/photo-1461749280684-dccba630e2f6?w=400&q=80"),
                new CourseData("UI/UX dizayn asoslari", "Nilufar Yusupova", "Dizayn",
                        4.7, 860, 199000, "https://images.unsplash.com/photo-1561070791-2526d30994b5?w=400&q=80"),
                new CourseData("Raqamli marketing va SMM", "Jasur Rahimov", "Biznes",
                        4.9, 2100, 179000, "https://images.unsplash.com/photo-1533750349088-cd871a92f312?w=400&q=80"),
                new CourseData("Python bilan sun'iy intellekt", "Dilnoza Saidova", "Dasturlash",
                        4.8, 1560, 299000, "https://images.unsplash.com/photo-1555949963-aa79dcee981c?w=400&q=80"),
                new CourseData("Backend dasturlash: Java asoslari", "Sardor Tojiyev", "Dasturlash",
                        4.6, 940, 279000, "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=400&q=80"),
                new CourseData("Ingliz tili: nutq va grammatika", "Malika Nazarova", "Tillar",
                        4.9, 3200, 149000, "https://images.unsplash.com/photo-1546410531-bb4caa6b424d?w=400&q=80"),
                new CourseData("Portret fotografiya siri", "Bekzod Umarov", "Fotografiya",
                        4.7, 540, 229000, "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=400&q=80"),
                new CourseData("Shaxsiy moliyani boshqarish", "Zarina Qodirova", "Moliya",
                        4.6, 780, 189000, "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?w=400&q=80"),
                new CourseData("Uy sharoitida fitnes dasturi", "Otabek Yo'ldoshev", "Sog'liq",
                        4.8, 1100, 159000, "https://images.unsplash.com/photo-1518611012118-696072aa579a?w=400&q=80"),
                new CourseData("Gitarada chalishni o'rganish", "Kamola Rashidova", "Musiqa",
                        4.9, 670, 169000, "https://images.unsplash.com/photo-1510915361894-db8b60106cb1?w=400&q=80"),
        };

        for (CourseData d : data) {
            Course course = new Course();
            course.setTitle(d.title());
            course.setSlug(SlugUtil.slugify(d.title()));
            course.setDescription("Bu kursda siz " + d.category().toLowerCase()
                    + " sohasida zarur bo'lgan barcha asosiy va amaliy ko'nikmalarni bosqichma-bosqich o'rganasiz.");
            course.setPriceAmount(d.price());
            course.setImageUrl(d.image());
            course.setStatus(CourseStatus.ACTIVE);
            course.setCategory(categories.get(d.category()));
            course.setInstructor(instructors.get(d.instructor()));
            course.setRatingAvg(d.rating());
            course.setRatingCount(Math.max(1, d.students() / 20));
            course.setStudentsCount(d.students());
            courseRepository.save(course);
        }
    }
}