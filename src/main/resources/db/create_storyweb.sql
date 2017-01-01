--liquibase formatted sql

--changeset storyweb:1
create table glossary (
  name varchar(50) primary key,
  description varchar(500)
)
