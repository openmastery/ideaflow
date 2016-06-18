--liquibase formatted sql

--changeset ideaflow:11
create sequence task_seq

--changeset ideaflow:12
create table task (
  id bigint constraint task_pk primary key,
  name varchar(50),
  description varchar(500),
  creation_date timestamp without time zone,
  unique (name)
)

