--liquibase formatted sql

--changeset ideaflow:11
create sequence task_seq

--changeset ideaflow:12
create table task (
  id bigint constraint task_pk primary key,
  name varchar(250),
  description varchar(500),
  unique (name)
)

