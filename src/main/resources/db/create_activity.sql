--liquibase formatted sql

--changeset activity:1
create sequence activity_seq

--changeset activity:2
create table activity (
  id bigint constraint activity_pk primary key,
  owner_id bigint not null,
  task_id bigint not null,
  start_time timestamp without time zone,
  end_time timestamp without time zone,
  type varchar(20),
  metadata varchar(1000)
)
