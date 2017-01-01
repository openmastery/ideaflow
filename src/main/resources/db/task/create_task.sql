--liquibase formatted sql

--changeset task:1
create sequence task_seq

--changeset task:2
create table task (
  id bigint constraint task_pk primary key,
  owner_id bigint not null,
  name varchar(50),
  description varchar(500),
  creation_date timestamp without time zone,
  constraint task_owner_id_name_ux unique (owner_id, name)
)
