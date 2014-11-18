# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table client (
  id                        bigint not null,
  f_name                    varchar(255),
  l_name                    varchar(255),
  email                     varchar(255),
  password                  varchar(255),
  constraint pk_client primary key (id))
;

create table order_currency (
  order_currency_id         bigint not null,
  constraint pk_order_currency primary key (order_currency_id))
;

create table orders (
  id                        bigint not null,
  first_name                varchar(255),
  last_name                 varchar(255),
  email                     varchar(255),
  client_id                 bigint,
  constraint pk_orders primary key (id))
;

create sequence client_seq;

create sequence order_currency_seq;

create sequence orders_seq;

alter table orders add constraint fk_orders_client_1 foreign key (client_id) references client (id);
create index ix_orders_client_1 on orders (client_id);



# --- !Downs

drop table if exists client cascade;

drop table if exists order_currency cascade;

drop table if exists orders cascade;

drop sequence if exists client_seq;

drop sequence if exists order_currency_seq;

drop sequence if exists orders_seq;

