package com.example.specdriven.workload;

import com.example.specdriven.member.Member;
import com.example.specdriven.member.MemberRepository;
import com.example.specdriven.task.Task;
import com.example.specdriven.task.TaskRepository;
import com.example.specdriven.task.TaskStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TeamWorkloadService {

    private final MemberRepository memberRepository;
    private final TaskRepository taskRepository;

    public TeamWorkloadService(MemberRepository memberRepository, TaskRepository taskRepository) {
        this.memberRepository = memberRepository;
        this.taskRepository = taskRepository;
    }

    public List<MemberWorkload> getWorkloads() {
        List<Member> members = memberRepository.findAll();
        List<Task> allTasks = taskRepository.findAll();

        Map<Long, List<Task>> tasksByMember = allTasks.stream()
                .filter(t -> t.getAssignee() != null)
                .collect(Collectors.groupingBy(t -> t.getAssignee().getId()));

        List<MemberWorkload> workloads = new ArrayList<>();
        for (Member member : members) {
            List<Task> memberTasks = tasksByMember.getOrDefault(member.getId(), List.of());
            long activeTasks = memberTasks.stream()
                    .filter(t -> t.getStatus() == TaskStatus.TODO || t.getStatus() == TaskStatus.IN_PROGRESS)
                    .count();
            long completedTasks = memberTasks.stream()
                    .filter(t -> t.getStatus() == TaskStatus.DONE)
                    .count();
            workloads.add(new MemberWorkload(member, memberTasks, activeTasks, completedTasks));
        }
        return workloads;
    }

    public record MemberWorkload(Member member, List<Task> tasks, long activeTasks, long completedTasks) {
        public boolean isOverloaded() {
            return activeTasks > 5;
        }
    }
}
