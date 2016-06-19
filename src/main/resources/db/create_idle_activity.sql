--liquibase formatted sql

--changeset ideaflow:3
create sequence idle_activity_seq

--changeset ideaflow:4
create table idle_activity (
  id bigint constraint idle_activity_pk primary key,
  task_id bigint not null,
  start_time timestamp without time zone,
  end_time timestamp without time zone,
  comment varchar(250),
  auto boolean
)
