# --- !Ups

create table students (
  id bigserial not null primary key,
  name varchar(256) default '' not null,
  last_name varchar(256) default '' not null,
  patronymic varchar(256) default '' not null,
  group_id bigint not null
    constraint fk_group references groups (id),
  department_id bigint
    constraint fk_department references departments (id) on delete set null
);

# --- !Downs

drop table if exists students;
