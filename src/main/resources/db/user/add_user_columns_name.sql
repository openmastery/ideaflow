--liquibase formatted sql

--changeset user:3
alter table om_user
add column name varchar(200);


