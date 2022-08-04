//package mk.ukim.finki.timskiproekt.model;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import mk.ukim.finki.timskiproekt.model.enums.RoomStatus;
//
//import javax.persistence.*;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//@Entity
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//public class Session {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    private LocalDateTime startTime;
//    private LocalDateTime endTime;
//    private UUID code;
//
//    @Enumerated(value = EnumType.STRING)
//    private RoomStatus status;
//
//    @JsonIgnore
//    @OneToMany(mappedBy = "session", fetch = FetchType.LAZY)
//    private List<StudentInRoom> students;
//
//    @OneToOne
//    private Chat chat;
//
//    public Session(Chat chat) {
//        this.startTime = LocalDateTime.now();
//        this.code = UUID.randomUUID();
//        this.status = RoomStatus.OPEN;
//        this.students = new ArrayList<>();
//        this.chat = chat;
//    }
//}