package com.example.dailyschedule.Repository;

import com.example.dailyschedule.Entity.Schedule;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class ScheduleRepositoryImpl implements ScheduleRepository {

    private final JdbcTemplate jdbcTemplate;


    public ScheduleRepositoryImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
        // 일정 저장하기. CREATE.
        @Override
        public Schedule saveSchedule(Schedule schedule){
            KeyHolder keyHolder = new GeneratedKeyHolder();

            LocalDateTime now = LocalDateTime.now();
            schedule.setCreatedAt(now);
            schedule.setUpdatedAt(now);

            // SQL 쿼리 수정: RETURNING id 추가
            /**
             * GeneratedKeyHolder는 기본적으로 단일 키(예: id)를 반환하도록 설계되었습니다.
             * 그러나 현재 SQL 쿼리에서 반환된 키 값에 여러 필드(id, task, password, member_name, created_at, updated_at)가 포함되어 있습니다.
             * 이로 인해 keyHolder.getKey() 호출 시 InvalidDataAccessApiUsageException이 발생합니다.
             * PostgreSQL에서 RETURNING * 또는 RETURNING id를 사용하지 않으면, 삽입된 모든 열의 값을 반환할 수 있습니다.
             * 현재 코드에서 Statement.RETURN_GENERATED_KEYS를 사용하고 있지만, PostgreSQL은 기본적으로 모든 열을 반환하려고 시도합니다.
             * 해결 방법
             * GeneratedKeyHolder를 올바르게 사용하려면, 삽입된 레코드에서 단일 키(예: id)만 반환하도록 SQL 쿼리를 수정해야 합니다.
             */
            
            // MySQL, MariaDB, H2 등에서 사용
            String sql = "INSERT INTO schedule (task, password, member_name, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
            // for postgresql
            // String sql = "INSERT INTO schedule (task, password, member_name, created_at, updated_at) VALUES (?, ?, ?, ?, ?) RETURNING id";
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, schedule.getTask());
                ps.setString(2, schedule.getPassword());
                ps.setString(3, schedule.getMemberName());
                ps.setTimestamp(4, java.sql.Timestamp.valueOf(schedule.getCreatedAt()));
                ps.setTimestamp(5, java.sql.Timestamp.valueOf(schedule.getUpdatedAt()));
                return ps;
            }, keyHolder);

            // 단일 키(id)만 가져오기
            schedule.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
            return schedule;            
        }
        // 일정 모두 찾기(조회). READ.
        @Override
        public List<Schedule> findAllSchedules (String updatedDate, String memberName){
            StringBuilder sql = new StringBuilder("SELECT id, task, member_name, created_at, updated_at FROM schedule WHERE 1=1 ");
            List<Object> params = new ArrayList<>();

            if (updatedDate != null && !updatedDate.isEmpty()) {
                sql.append("AND DATE(updated_at) = ? ");
                params.add(updatedDate);
            }
            if (memberName != null && !memberName.isEmpty()) {
                sql.append("AND member_name = ? ");
                params.add(memberName);
            }
            sql.append("ORDER BY updated_at DESC");

            return jdbcTemplate.query(
                    sql.toString(),
                    (rs, rowNum) -> new Schedule(
                            rs.getLong("id"),
                            rs.getString("task"),
                            rs.getString("member_name"),
                            rs.getTimestamp("created_at").toLocalDateTime(),
                            rs.getTimestamp("updated_at").toLocalDateTime()
                    ),
                    params.toArray(new Object[0])
            );

        }
        // 일정을 특정 ID로 찾기. USE.
        @Override
        public Optional<Schedule> findScheduleById(Long id){
            String sql = "SELECT id, task, password, member_name, created_at, updated_at FROM schedule WHERE id = ?";
            List<Schedule> list = jdbcTemplate.query(sql, (rs, rowNum) -> new Schedule(
                    rs.getLong("id"),
                    rs.getString("task"),
                    rs.getString("password"),
                    rs.getString("member_name"),
                    rs.getTimestamp("created_at").toLocalDateTime(),
                    rs.getTimestamp("updated_at").toLocalDateTime()
            ), id);
            return list.stream().findAny();
        }
        // 일정 수정하기.
        @Override
        public Schedule updateSchedule(Schedule schedule){

            LocalDateTime now = LocalDateTime.now();
            schedule.setUpdatedAt(now);

            String sql = "UPDATE schedule SET task = ?, member_name = ?, updated_at=? WHERE id = ?";
            jdbcTemplate.update(sql, schedule.getTask(), schedule.getMemberName(), schedule.getUpdatedAt(), schedule.getId());
            return findScheduleById(schedule.getId()).orElseThrow(() -> new IllegalStateException("일정 수정 후 조회 실패"));
        }
        // 일정 삭제하기. DELETE
        @Override
        public void deleteSchedule (Long id){
            String sql = "DELETE FROM schedule WHERE id = ?";
            jdbcTemplate.update(sql, id);
        }
    }


