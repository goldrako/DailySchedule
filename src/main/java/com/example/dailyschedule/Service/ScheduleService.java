package com.example.dailyschedule.Service;

import com.example.dailyschedule.Entity.Schedule;
import com.example.dailyschedule.Repository.ScheduleRepository;
import com.example.dailyschedule.dto.request.ScheduleRequestDto;
import com.example.dailyschedule.dto.response.ScheduleResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    @Transactional
    public ScheduleResponseDto saveSchedule(ScheduleRequestDto requestDto) {
        Schedule schedule = new Schedule(
                requestDto.getTask(),
                requestDto.getPassword(),
                requestDto.getMemberName()
        );
        Schedule savedSchedule = scheduleRepository.saveSchedule(schedule);
        return new ScheduleResponseDto(
                savedSchedule.getId(),
                savedSchedule.getTask(),
                savedSchedule.getMemberName(),
                savedSchedule.getCreatedAt(),
                savedSchedule.getUpdatedAt()
        );
    }

    @Transactional(readOnly = true)
    public ScheduleResponseDto findScheduleById(Long id) {
        Schedule schedule = scheduleRepository.findScheduleById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 id의 스케줄이 존재하지 않습니다.")
        );
        return new ScheduleResponseDto(
                schedule.getId(),
                schedule.getTask(),
                schedule.getMemberName(),
                schedule.getCreatedAt(),
                schedule.getUpdatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> findAllSchedules(String updatedDate, String memberName) {
        List<Schedule> schedules = scheduleRepository.findAllSchedules(updatedDate, memberName);

        return schedules.stream().map(schedule -> new ScheduleResponseDto(
                schedule.getId(),
                schedule.getTask(),
                schedule.getMemberName(),
                schedule.getCreatedAt(),
                schedule.getUpdatedAt())).collect(Collectors.toList());
    }

    @Transactional
    public ScheduleResponseDto updateSchedule(Long id, ScheduleRequestDto request) {
        Schedule schedule = scheduleRepository.findScheduleById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 id의 스케줄이 존재하지 않습니다.")
        );
        if (!schedule.getPassword().equals(request.getPassword())) {
            throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
        }
        schedule.update(request.getTask(), request.getMemberName());
        // Schedule updatedSchedule = scheduleRepository.saveSchedule(schedule);
        Schedule updatedSchedule = scheduleRepository.updateSchedule(schedule); // 수정된 부분

        return new ScheduleResponseDto(
                updatedSchedule.getId(),
                updatedSchedule.getTask(),
                updatedSchedule.getMemberName(),
                updatedSchedule.getCreatedAt(),
                updatedSchedule.getUpdatedAt()
        );

    }
    @Transactional
    public void deleteSchedule (Long id, String password){
        Schedule schedule = scheduleRepository.findScheduleById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 id의 스케줄이 존재하지 않습니다.")
        );

        if (!schedule.getPassword().equals(password)) {
            throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
        }
        scheduleRepository.deleteSchedule(id);
    }
}