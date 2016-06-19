--liquibase formatted sql

--changeset ideaflow:3
create sequence idle_time_band_seq

--changeset ideaflow:4
create table idle_time_band (
  id bigint constraint idle_time_band_pk primary key,
  task_id bigint not null,
  start_time timestamp without time zone,
  end_time timestamp without time zone,
  comment varchar(250),
  auto boolean
)
