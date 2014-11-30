# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table additions (
  id                        bigint not null,
  ui_label                  varchar(255),
  additional_price          float,
  constraint pk_additions primary key (id))
;

create table client (
  id                        bigint not null,
  f_name                    varchar(255),
  l_name                    varchar(255),
  email                     varchar(255),
  password                  varchar(255),
  countries_id              bigint,
  constraint pk_client primary key (id))
;

create table countries (
  id                        bigint not null,
  iso                       varchar(255),
  name                      varchar(255),
  nicename                  varchar(255),
  iso3                      varchar(255),
  numcode                   integer,
  phonecode                 integer,
  constraint pk_countries primary key (id))
;

create table deadline_deadline_category_association (
  id                        bigint not null,
  order_deadlines_id        bigint,
  order_deadline_category_id bigint,
  additional_price          float,
  constraint pk_deadline_deadline_category_as primary key (id))
;

create table freelance_writer (
  freelance_writer_id       bigint not null,
  f_name                    varchar(255),
  l_name                    varchar(255),
  constraint pk_freelance_writer primary key (freelance_writer_id))
;

create table order_cpp_mode (
  id                        bigint not null,
  order_cpp_mode_name       integer,
  cpp_mode_description      varchar(255),
  constraint ck_order_cpp_mode_order_cpp_mode_name check (order_cpp_mode_name in (0,1,2)),
  constraint pk_order_cpp_mode primary key (id))
;

create table order_currence (
  order_currency_id         bigint not null,
  currency_name             varchar(255),
  currency_symbol           varchar(255),
  currency_symbol_2         varchar(255),
  convertion_rate           float,
  constraint pk_order_currence primary key (order_currency_id))
;

create table order_deadline_category (
  id                        bigint not null,
  order_deadline_category_name varchar(255),
  order_deadline_category_description varchar(255),
  constraint pk_order_deadline_category primary key (id))
;

create table order_deadlines (
  id                        bigint not null,
  deadline_value            integer,
  deadline_unit             varchar(255),
  seconds_elapsing_to_deadline bigint,
  constraint pk_order_deadlines primary key (id))
;

create table order_document_type (
  id                        bigint not null,
  document_type_name        varchar(255),
  base_price                float,
  description               varchar(255),
  order_deadline_category_id bigint,
  order_cpp_mode_id         bigint,
  constraint pk_order_document_type primary key (id))
;

create table order_level_of_writing (
  id                        bigint not null,
  order_level               varchar(255),
  additional_price          float,
  description               varchar(255),
  constraint pk_order_level_of_writing primary key (id))
;

create table order_messages (
  id                        bigint not null,
  msg_to                    integer,
  msg_from                  integer,
  status                    boolean default TRUE,
  orders_id                 bigint,
  sent_on                   timestamp not null,
  message                   varchar(255),
  constraint ck_order_messages_msg_to check (msg_to in (0,1,2)),
  constraint ck_order_messages_msg_from check (msg_from in (0,1,2)),
  constraint pk_order_messages primary key (id))
;

create table order_subject (
  id                        bigint not null,
  subject_name              varchar(255),
  description               varchar(255),
  order_subject_category_id bigint,
  constraint pk_order_subject primary key (id))
;

create table order_subject_category (
  id                        bigint not null,
  subject_category_name     varchar(255),
  additional_price          float,
  description               varchar(255),
  constraint pk_order_subject_category primary key (id))
;

create table orders (
  id                        bigint not null,
  first_name                varchar(255),
  last_name                 varchar(255),
  email                     varchar(255),
  client_id                 bigint,
  order_level_of_writing_id bigint,
  order_document_type_id    bigint,
  order_currence_order_currency_id bigint,
  spacing_id                bigint,
  constraint pk_orders primary key (id))
;

create table preferred_writer (
  preferred_writer_entry_id bigint not null,
  client_id                 bigint,
  freelance_writer_freelance_writer_id bigint,
  constraint pk_preferred_writer primary key (preferred_writer_entry_id))
;

create table spacing (
  id                        bigint not null,
  spacing                   varchar(255),
  alias                     varchar(255),
  factor                    integer,
  constraint pk_spacing primary key (id))
;

create table writer_support (
  id                        bigint not null,
  f_name                    varchar(255),
  constraint pk_writer_support primary key (id))
;


create table order_subject_order_document_typ (
  order_subject_id               bigint not null,
  order_document_type_id         bigint not null,
  constraint pk_order_subject_order_document_typ primary key (order_subject_id, order_document_type_id))
;

create table orders_additions (
  orders_id                      bigint not null,
  additions_id                   bigint not null,
  constraint pk_orders_additions primary key (orders_id, additions_id))
;
create sequence additions_seq;

create sequence client_seq;

create sequence countries_seq;

create sequence deadline_deadline_category_association_seq;

create sequence freelance_writer_seq;

create sequence order_cpp_mode_seq;

create sequence order_currence_seq;

create sequence order_deadline_category_seq;

create sequence order_deadlines_seq;

create sequence order_document_type_seq;

create sequence order_level_of_writing_seq;

create sequence order_messages_seq;

create sequence order_subject_seq;

create sequence order_subject_category_seq;

create sequence orders_seq;

create sequence preferred_writer_seq;

create sequence spacing_seq;

create sequence writer_support_seq;

alter table client add constraint fk_client_countries_1 foreign key (countries_id) references countries (id);
create index ix_client_countries_1 on client (countries_id);
alter table deadline_deadline_category_association add constraint fk_deadline_deadline_category__2 foreign key (order_deadlines_id) references order_deadlines (id);
create index ix_deadline_deadline_category__2 on deadline_deadline_category_association (order_deadlines_id);
alter table deadline_deadline_category_association add constraint fk_deadline_deadline_category__3 foreign key (order_deadline_category_id) references order_deadline_category (id);
create index ix_deadline_deadline_category__3 on deadline_deadline_category_association (order_deadline_category_id);
alter table order_document_type add constraint fk_order_document_type_orderDe_4 foreign key (order_deadline_category_id) references order_deadline_category (id);
create index ix_order_document_type_orderDe_4 on order_document_type (order_deadline_category_id);
alter table order_document_type add constraint fk_order_document_type_orderCp_5 foreign key (order_cpp_mode_id) references order_cpp_mode (id);
create index ix_order_document_type_orderCp_5 on order_document_type (order_cpp_mode_id);
alter table order_messages add constraint fk_order_messages_orders_6 foreign key (orders_id) references orders (id);
create index ix_order_messages_orders_6 on order_messages (orders_id);
alter table order_subject add constraint fk_order_subject_orderSubjectC_7 foreign key (order_subject_category_id) references order_subject_category (id);
create index ix_order_subject_orderSubjectC_7 on order_subject (order_subject_category_id);
alter table orders add constraint fk_orders_client_8 foreign key (client_id) references client (id);
create index ix_orders_client_8 on orders (client_id);
alter table orders add constraint fk_orders_orderLevelOfWriting_9 foreign key (order_level_of_writing_id) references order_level_of_writing (id);
create index ix_orders_orderLevelOfWriting_9 on orders (order_level_of_writing_id);
alter table orders add constraint fk_orders_orderDocumentType_10 foreign key (order_document_type_id) references order_document_type (id);
create index ix_orders_orderDocumentType_10 on orders (order_document_type_id);
alter table orders add constraint fk_orders_orderCurrence_11 foreign key (order_currence_order_currency_id) references order_currence (order_currency_id);
create index ix_orders_orderCurrence_11 on orders (order_currence_order_currency_id);
alter table orders add constraint fk_orders_spacing_12 foreign key (spacing_id) references spacing (id);
create index ix_orders_spacing_12 on orders (spacing_id);
alter table preferred_writer add constraint fk_preferred_writer_client_13 foreign key (client_id) references client (id);
create index ix_preferred_writer_client_13 on preferred_writer (client_id);
alter table preferred_writer add constraint fk_preferred_writer_freelance_14 foreign key (freelance_writer_freelance_writer_id) references freelance_writer (freelance_writer_id);
create index ix_preferred_writer_freelance_14 on preferred_writer (freelance_writer_freelance_writer_id);



alter table order_subject_order_document_typ add constraint fk_order_subject_order_docume_01 foreign key (order_subject_id) references order_subject (id);

alter table order_subject_order_document_typ add constraint fk_order_subject_order_docume_02 foreign key (order_document_type_id) references order_document_type (id);

alter table orders_additions add constraint fk_orders_additions_orders_01 foreign key (orders_id) references orders (id);

alter table orders_additions add constraint fk_orders_additions_additions_02 foreign key (additions_id) references additions (id);

# --- !Downs

drop table if exists additions cascade;

drop table if exists orders_additions cascade;

drop table if exists client cascade;

drop table if exists countries cascade;

drop table if exists deadline_deadline_category_association cascade;

drop table if exists freelance_writer cascade;

drop table if exists order_cpp_mode cascade;

drop table if exists order_currence cascade;

drop table if exists order_deadline_category cascade;

drop table if exists order_deadlines cascade;

drop table if exists order_document_type cascade;

drop table if exists order_subject_order_document_typ cascade;

drop table if exists order_level_of_writing cascade;

drop table if exists order_messages cascade;

drop table if exists order_subject cascade;

drop table if exists order_subject_category cascade;

drop table if exists orders cascade;

drop table if exists preferred_writer cascade;

drop table if exists spacing cascade;

drop table if exists writer_support cascade;

drop sequence if exists additions_seq;

drop sequence if exists client_seq;

drop sequence if exists countries_seq;

drop sequence if exists deadline_deadline_category_association_seq;

drop sequence if exists freelance_writer_seq;

drop sequence if exists order_cpp_mode_seq;

drop sequence if exists order_currence_seq;

drop sequence if exists order_deadline_category_seq;

drop sequence if exists order_deadlines_seq;

drop sequence if exists order_document_type_seq;

drop sequence if exists order_level_of_writing_seq;

drop sequence if exists order_messages_seq;

drop sequence if exists order_subject_seq;

drop sequence if exists order_subject_category_seq;

drop sequence if exists orders_seq;

drop sequence if exists preferred_writer_seq;

drop sequence if exists spacing_seq;

drop sequence if exists writer_support_seq;

