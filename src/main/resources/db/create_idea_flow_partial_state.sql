--liquibase formatted sql

--changeset ideaflow:9
create type idea_flow_partial_state_scope as enum ('active', 'containing')
--rollback drop type idea_flow_partial_state_scope

--changeset ideaflow:10
create table idea_flow_partial_state (
  scope idea_flow_partial_state_scope,
  task_id bigint,
  type varchar(15)
  start timestamp without time zone,
  starting_comment varchar(250),
  is_nested boolean,
  is_linked_to_previous boolean
)
--rollback drop table idea_flow_partial_state
