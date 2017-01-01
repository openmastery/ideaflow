--liquibase formatted sql

--changeset storyweb:1
create table glossary (
  name varchar(50) constraint glossary_pk primary key,
  description varchar(500)
)
