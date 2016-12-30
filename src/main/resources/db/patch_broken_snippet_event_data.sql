--liquibase formatted sql

--changeset event:3
update event
set type = 'WTF' where type is null;



