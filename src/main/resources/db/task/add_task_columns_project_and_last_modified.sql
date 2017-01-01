--liquibase formatted sql

--changeset task:3
alter table task
add column modify_date timestamp without time zone;

--changeset task:4
alter table task
add column project varchar(50);


