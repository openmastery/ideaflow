--liquibase formatted sql

--changeset foreignkeys:1
alter table activity add foreign key(task_id) references task(id) on delete cascade

--changeset foreignkeys:2
alter table annotation add foreign key(task_id) references task(id) on delete cascade

--changeset foreignkeys:3
alter table event add foreign key(task_id) references task(id) on delete cascade

--changeset foreignkeys:4
alter table idea_flow_state add foreign key(task_id) references task(id) on delete cascade;

--changeset foreignkeys:5
alter table idea_flow_partial_state add foreign key(task_id) references task(id) on delete cascade
