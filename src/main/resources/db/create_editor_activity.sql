--liquibase formatted sql

--changeset ideaflow:7
create sequence editor_activity_seq

--changeset ideaflow:8
create table editor_activity (
  id bigint constraint editor_activity_pk primary key,
  task_id bigint,
  start_time timestamp without time zone,
  end_time timestamp without time zone,
  file_path varchar(500),
  is_modified boolean
)
