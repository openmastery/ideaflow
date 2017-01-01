--liquibase formatted sql

--changeset ideaflowstate:1
create sequence idea_flow_state_seq

--changeset ideaflowstate:2
create table idea_flow_state (
  id bigint constraint idea_flow_state_pk primary key,
  owner_id bigint not null,
  task_id bigint,
  type varchar(15),
  start_time timestamp without time zone,
  end_time timestamp without time zone,
  starting_comment varchar(250),
  ending_comment varchar(250),
  is_nested boolean,
  is_linked_to_previous boolean
)
