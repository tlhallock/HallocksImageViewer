

CREATE DATABASE images;
GRANT ALL PRIVILEGES ON images.* TO 'imagesuser'@'localhost' IDENTIFIED BY 'imagepass';
FLUSH PRIVILEGES;

mysql -u imageuser -p

use images;




drop table TAGS;
drop table PEOPLE;
drop table USERS;
drop table COMMENTS;
drop table IMAGES;
drop table ROOTS;




create table if not exists ROOTS (
   R_ID integer primary key     auto_increment not null,
   URL          text(1024)      not null,
   PATH         text(1024)      not null
);
create unique index ROOTS_INDEX_UNIQUE_PATH on ROOTS(PATH(1024));

create table if not exists  IMAGES (
   I_ID              integer        primary key     auto_increment,
   PATH              text(1024)     not null,
   CHECKSUM          varchar(40),
   IMAGE_TIME        timestamp      not null default '1970-01-01 00:00:01',
   LAST_MODIFIED     timestamp      not null default '1970-01-01 00:00:01',
   RID               int            not null,
   TIME_PATH         text(11)       not null, # 'yyyy/mm/dd/'
   
   foreign key(RID ) references ROOTS  (R_ID)  on delete cascade
);
create unique index IMAGES_INDEX_UNIQUE_PATH on IMAGES(RID, PATH(1024));
# Should create an index on the TIME_PATH to speed it up...


create table if not exists COMMENTS (
   C_ID         integer primary key   auto_increment,
   COMMENT      text(1024)            not null,
   IID          int                   not null,
   POSTED       timestamp             not null default '1970-01-01 00:00:01',
   
   foreign key(IID) references IMAGES  (I_ID)  on delete cascade
);


create table if not exists USERS (
   U_ID         integer   primary key     auto_increment,
   USERNAME     text(256) not null,
   PASSWORD     text(256) not null,
   DELETEPERM   BOOLEAN   not null,
   UPLOADPERM   BOOLEAN   not null
);

create table if not exists TAGS (
   T_ID         integer   primary key     auto_increment,
   NAME         char(64)  not null,
   IID          int       not null,
   
   foreign key(IID ) references IMAGES  (I_ID )  on delete cascade
);

create table if not exists PEOPLE (
   C_ID         integer    primary key     auto_increment,
   COMMENT      text(1024) not null,
   IID          int        not null,
   
   foreign key(IID) references IMAGES (I_ID )  on delete cascade
);

# videos




# CREATE UNIQUE      INDEX IF NOT EXISTS MACHINE_INDEX_UNIQUE_ADDRESS    ON MACHINE(IP, PORT);
# CREATE UNIQUE HASH INDEX IF NOT EXISTS MACHINE_INDEX_UNIQUE_IDENTIFIER ON MACHINE(IDENT);
# CREATE             INDEX IF NOT EXISTS MACHINE_INDEX_FAST_FIND         ON MACHINE(M_ID);




