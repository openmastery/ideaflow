--liquibase formatted sql

--changeset ideaflow:7
create sequence editor_activity_seq
--rollback drop sequence editor_activity_seq

--changeset ideaflow:8
create table editor_activity (
  id bigint constraint event_pk primary key,
  task_id bigint,
  start timestamp without time zone,
  end timestamp without time zone,
  file_path varchar(500),
  is_modified boolean
)
--rollback drop table editor_activity
