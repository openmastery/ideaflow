--liquibase formatted sql

--changeset storyweb:1
create sequence glossary_seq


--changeset storyweb:2
create table glossary (
  id bigint constraint glossary_pk primary key,
  name varchar(50),
  description varchar(500)
)
