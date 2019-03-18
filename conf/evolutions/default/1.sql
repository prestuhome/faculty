# --- !Ups

create table departments (
  id bigserial not null primary key,
  name varchar(256) default '' not null
);

create table groups (
  id bigserial not null primary key,
  code varchar(256) default '' not null
);

# --- !Downs

drop table if exists departments;
drop table if exists groups;
