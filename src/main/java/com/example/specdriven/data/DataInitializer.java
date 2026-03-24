package com.example.specdriven.data;

import com.example.specdriven.member.Member;
import com.example.specdriven.member.MemberRepository;
import com.example.specdriven.member.MemberRole;
import com.example.specdriven.project.Project;
import com.example.specdriven.project.ProjectRepository;
import com.example.specdriven.task.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final MemberRepository memberRepository;
    private final TaskDependencyRepository taskDependencyRepository;

    public DataInitializer(ProjectRepository projectRepository,
                           TaskRepository taskRepository,
                           MemberRepository memberRepository,
                           TaskDependencyRepository taskDependencyRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.memberRepository = memberRepository;
        this.taskDependencyRepository = taskDependencyRepository;
    }

    @Override
    public void run(String... args) {
        if (projectRepository.count() > 0) {
            return;
        }

        // Members
        Member alice = memberRepository.save(new Member("Alice Johnson", "alice@forge.dev", MemberRole.MANAGER));
        Member bob = memberRepository.save(new Member("Bob Smith", "bob@forge.dev", MemberRole.DEVELOPER));
        Member carol = memberRepository.save(new Member("Carol Davis", "carol@forge.dev", MemberRole.DEVELOPER));
        Member dave = memberRepository.save(new Member("Dave Wilson", "dave@forge.dev", MemberRole.DESIGNER));
        Member eve = memberRepository.save(new Member("Eve Martinez", "eve@forge.dev", MemberRole.QA));

        // Project 1: Website Redesign
        Project p1 = projectRepository.save(new Project(
                "Website Redesign",
                "Complete overhaul of the company website with modern design",
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 6, 30)));

        Task t1 = new Task("Design wireframes", LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 15), p1);
        t1.setAssignee(dave);
        t1.setStatus(TaskStatus.DONE);
        t1.setPriority(TaskPriority.HIGH);
        t1 = taskRepository.save(t1);

        Task t2 = new Task("Implement homepage", LocalDate.of(2026, 3, 16), LocalDate.of(2026, 4, 15), p1);
        t2.setAssignee(bob);
        t2.setStatus(TaskStatus.IN_PROGRESS);
        t2.setPriority(TaskPriority.HIGH);
        t2 = taskRepository.save(t2);

        Task t3 = new Task("Build contact form", LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 30), p1);
        t3.setAssignee(carol);
        t3.setStatus(TaskStatus.TODO);
        t3.setPriority(TaskPriority.MEDIUM);
        t3 = taskRepository.save(t3);

        Task t4 = new Task("QA testing", LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31), p1);
        t4.setAssignee(eve);
        t4.setStatus(TaskStatus.TODO);
        t4.setPriority(TaskPriority.HIGH);
        t4 = taskRepository.save(t4);

        taskDependencyRepository.save(new TaskDependency(t1, t2));
        taskDependencyRepository.save(new TaskDependency(t2, t4));
        taskDependencyRepository.save(new TaskDependency(t3, t4));

        // Project 2: Mobile App MVP
        Project p2 = projectRepository.save(new Project(
                "Mobile App MVP",
                "First version of the mobile application",
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 9, 30)));

        Task t5 = new Task("Define user stories", LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 14), p2);
        t5.setAssignee(alice);
        t5.setStatus(TaskStatus.TODO);
        t5.setPriority(TaskPriority.HIGH);
        taskRepository.save(t5);

        Task t6 = new Task("UI/UX design", LocalDate.of(2026, 4, 15), LocalDate.of(2026, 5, 15), p2);
        t6.setAssignee(dave);
        t6.setStatus(TaskStatus.TODO);
        t6.setPriority(TaskPriority.MEDIUM);
        taskRepository.save(t6);

        // Project 3: API Integration (empty — NOT_STARTED)
        projectRepository.save(new Project(
                "API Integration",
                "Integrate with third-party payment API",
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 7, 31)));
    }
}
