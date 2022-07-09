package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.StudentComparator;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {

    public static int count = 0;


    Logger studentLogger = LoggerFactory.getLogger(StudentServiceImpl.class);

    private StudentRepository studentRepository;

    public StudentServiceImpl ( StudentRepository studentRepository ) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent ( Student student ) {
        studentLogger.info("Вызван метод для создания студента");
        return studentRepository.save(student);
    }

    public Student readStudent ( Long id ) {
        studentLogger.info("Вызван метод для поиска студента");
        return studentRepository.findById(id).orElseThrow();
    }

    public Student updateStudent ( Student student ) {
        studentLogger.info("Вызван метод для обновления студента");
        return studentRepository.save(student);
    }

    public void deleteStudent ( Long id ) {
        studentLogger.info("Вызван метод для удаления студента");
        studentRepository.deleteById(id);
    }

    public Collection<Student> findByAgeBetween ( int minAge, int maxAge ) {
        studentLogger.info("Вызван метод для поиска студентов между минимальным и максимальным возрастом");
        return studentRepository.findByAgeBetween(minAge, maxAge);
    }

    public int getStudentsAmount () {
        studentLogger.info("Вызван метод для подсчета числа студентов");
        return studentRepository.getStudentsAmount();
    }

    public int getStudentsAverageAge () {
        studentLogger.info("Вызван метод для вывода среднего возраста студентов");
        return studentRepository.getStudentsAverageAge();
    }

    public List<Student> getLastFiveStudents () {
        studentLogger.info("Вызван метод для вывода последних пяти студентов");
        return studentRepository.getLastFiveStudents();
    }

    public Collection<Student> getStudentsWithFirstLetterAsA () {
        return studentRepository.findAll().stream()
                .parallel()
                .filter(student -> student.getName().startsWith("S"))
                .sorted(new StudentComparator())
                .collect(Collectors.toList());
    }

    public double getAverageAge () {
        return studentRepository.findAll().stream()
                .parallel()
                .collect(Collectors.averagingDouble(student -> student.getAge()));
    }

    // Несинхронизированные потоки с методом printStudentName
    public static void printStudentName ( List<String> names, int nameIndex ) {
        System.out.println(names.get(nameIndex) + " " + count);
        count++;
    }

    public void getAllStudentNames () {
        List<String> studentNames = studentRepository.findAll().stream()
                .map(s -> s.getName())
                .collect(Collectors.toList());

        new Thread(() -> {
            printStudentName(studentNames, 2);
            printStudentName(studentNames, 3);
        }).start();

        printStudentName(studentNames, 0);
        printStudentName(studentNames, 1);

        new Thread(() -> {
            printStudentName(studentNames, 4);
            printStudentName(studentNames, 5);
        }).start();
    }

    // Синхронизированные потоки
    public static void printStudentNameSync ( List<String> names, int nameIndex ) {
        synchronized (StudentServiceImpl.class) {
            System.out.println(names.get(nameIndex) + " " + count);
            count++;
        }
    }

    public void getAllStudentNamesSync () {
        List<String> studentNames = studentRepository.findAll().stream()
                .map(s -> s.getName())
                .collect(Collectors.toList());

        new Thread(() -> {
            printStudentNameSync(studentNames, 2);
            printStudentNameSync(studentNames, 3);
        }).start();

        printStudentNameSync(studentNames, 0);
        printStudentNameSync(studentNames, 1);

        new Thread(() -> {
            printStudentNameSync(studentNames, 4);
            printStudentNameSync(studentNames, 5);
        }).start();
    }
}
