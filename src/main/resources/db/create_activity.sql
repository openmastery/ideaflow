--liquibase formatted sql

--changeset ideaflow:7
create sequence activity_seq

--changeset ideaflow:8
create table activity (
  id bigint constraint activity_pk primary key,
  task_id bigint not null,
  start_time timestamp without time zone,
  end_time timestamp without time zone,
  metadata varchar(1000)
)
