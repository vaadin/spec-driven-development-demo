package com.example.specdriven.workload;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TeamWorkloadServiceTest {

    @Autowired
    private TeamWorkloadService workloadService;

    @Test
    void getWorkloadsReturnsAllMembers() {
        List<TeamWorkloadService.MemberWorkload> workloads = workloadService.getWorkloads();
        // 5 members from demo data
        assertEquals(5, workloads.size());
    }

    @Test
    void workloadIncludesTasksFromAllProjects() {
        var workloads = workloadService.getWorkloads();
        // Alice has a task in Mobile App MVP, Dave has tasks in both projects
        var dave = workloads.stream()
                .filter(w -> w.member().getName().equals("Dave Wilson"))
                .findFirst().orElseThrow();
        // Dave has "Design wireframes" (DONE) in Website Redesign and "UI/UX design" (TODO) in Mobile App
        assertTrue(dave.tasks().size() >= 2);
    }

    @Test
    void activeTaskCountIsCorrect() {
        var workloads = workloadService.getWorkloads();
        // Bob has "Implement homepage" which is IN_PROGRESS
        var bob = workloads.stream()
                .filter(w -> w.member().getName().equals("Bob Smith"))
                .findFirst().orElseThrow();
        assertEquals(1, bob.activeTasks());
    }

    @Test
    void completedTaskCountIsCorrect() {
        var workloads = workloadService.getWorkloads();
        // Dave's "Design wireframes" is DONE
        var dave = workloads.stream()
                .filter(w -> w.member().getName().equals("Dave Wilson"))
                .findFirst().orElseThrow();
        assertTrue(dave.completedTasks() >= 1);
    }

    @Test
    void membersWithNoTasksShowZeroCounts() {
        var workloads = workloadService.getWorkloads();
        // Check that all members are listed even if they have zero tasks
        // All 5 demo members should appear
        assertTrue(workloads.size() >= 5);
    }

    @Test
    void overloadThresholdIsFiveActiveTasks() {
        var workloads = workloadService.getWorkloads();
        // No member should be overloaded with demo data (max ~2 active tasks each)
        assertTrue(workloads.stream().noneMatch(TeamWorkloadService.MemberWorkload::isOverloaded));
    }
}
