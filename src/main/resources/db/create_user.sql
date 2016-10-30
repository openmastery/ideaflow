--liquibase formatted sql

--changeset user:1
create sequence user_seq

--changeset user:2
create table om_user (
  id bigint constraint user_pk primary key,
  email varchar(100),
  api_key varchar(40),
  unique (email),
  unique (api_key)
)

