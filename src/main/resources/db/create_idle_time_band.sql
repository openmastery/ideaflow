--liquibase formatted sql

--changeset ideaflow:3
create sequence idle_time_band_seq
--rollback drop sequence idle_time_band_seq

--changeset ideaflow:4
create table idle_time_band (
  id bigint constraint idle_time_band_pk primary key,
  task_id bigint,
  start timestamp without time zone,
  end timestamp without time zone,
  comment varchar(250),
  auto boolean,
)
--rollback drop table idle_time_band
