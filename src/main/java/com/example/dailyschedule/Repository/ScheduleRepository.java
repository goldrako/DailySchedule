package com.example.dailyschedule.Repository;

import com.example.dailyschedule.Entity.Schedule;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository {

    Schedule saveSchedule(Schedule schedule);

    List<Schedule> findAllSchedules(String updatedDate, String memberName);

    Optional<Schedule> findScheduleById(Long id);

    Schedule updateSchedule(Schedule schedule);

    void deleteSchedule(Long id);


}
