--liquibase formatted sql

--changeset ideaflowpartialstate:1
create table idea_flow_partial_state (
  task_id bigint,
  owner_id bigint not null,
  scope varchar(15),
  type varchar(15),
  start_time timestamp without time zone,
  starting_comment varchar(250),
  is_nested boolean,
  is_linked_to_previous boolean,
  constraint idea_flow_partial_state_pk primary key (task_id, scope)
)
