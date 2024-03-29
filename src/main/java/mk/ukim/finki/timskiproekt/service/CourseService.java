package mk.ukim.finki.timskiproekt.service;

import mk.ukim.finki.timskiproekt.model.Course;
import mk.ukim.finki.timskiproekt.model.Room;
import mk.ukim.finki.timskiproekt.model.Student;
import mk.ukim.finki.timskiproekt.model.dto.CourseDTO;
import mk.ukim.finki.timskiproekt.model.enums.Semester;

import java.util.List;

public interface CourseService {
    Course getCourseByName(String name);

    Course getCourseByCode(String code);

    List<Course> getAllCourses();

    List<Course> getAllCoursesBySemester(Semester semester);

    List<Room> getAllRoomsByCourse(String courseCode);

    List<Student> getAllStudentsInCourse(String courseCode);

    List<Student> getAllStudentsNotInCourse(String courseCode);

    Course createCourse(CourseDTO courseDTO);

    void deleteCourseByName(String name);

    Room addRoomToCourse(Room room, String courseCode);

    void deleteRoomFromCourse(Room room, String courseCode);

    void addStudentsToCourse(List<String> studentIndexes, String courseCode);
}
