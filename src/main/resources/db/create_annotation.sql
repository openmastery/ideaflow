--liquibase formatted sql

--changeset annotation:1
create sequence annotation_seq

--changeset annotation:2
create table annotation (
  id bigint constraint annotation_pk primary key,
  owner_id bigint not null,
  task_id bigint not null,
  event_id bigint not null,
  type varchar(20),
  metadata varchar(1000)
)
