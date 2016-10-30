--liquibase formatted sql

--changeset event:1
create sequence event_seq

--changeset event:2
create table event (
  id bigint constraint event_pk primary key,
  owner_id bigint not null,
  task_id bigint,
  type varchar(15),
  position timestamp without time zone,
  comment varchar(250)
)
