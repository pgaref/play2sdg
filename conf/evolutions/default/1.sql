# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table rating (
  id                        bigint not null,
  folder                    varchar(255),
  usermail                  varchar(255),
  constraint pk_rating primary key (id))
;

create table song (
  id                        integer not null,
  title                     varchar(255),
  artist                    varchar(255),
  release_date              timestamp,
  link                      varchar(255),
  constraint pk_song primary key (id))
;

create table user (
  email                     varchar(255) not null,
  name                      varchar(255),
  password                  varchar(255),
  constraint pk_user primary key (email))
;


create table rating_song (
  rating_id                      bigint not null,
  song_id                        integer not null,
  constraint pk_rating_song primary key (rating_id, song_id))
;
create sequence rating_seq;

create sequence song_seq;

create sequence user_seq;




alter table rating_song add constraint fk_rating_song_rating_01 foreign key (rating_id) references rating (id) on delete restrict on update restrict;

alter table rating_song add constraint fk_rating_song_song_02 foreign key (song_id) references song (id) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists rating;

drop table if exists rating_song;

drop table if exists song;

drop table if exists user;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists rating_seq;

drop sequence if exists song_seq;

drop sequence if exists user_seq;

