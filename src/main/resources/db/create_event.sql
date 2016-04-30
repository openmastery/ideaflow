--liquibase formatted sql

--changeset ideaflow:5
create sequence event_seq
--rollback drop sequence event_seq

--changeset ideaflow:6
create table event (
  id bigint constraint event_pk primary key,
  task_id bigint,
  type varchar(15)
  position timestamp without time zone,
  comment varchar(250)
)
--rollback drop table event
