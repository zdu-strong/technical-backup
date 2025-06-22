-- liquibase formatted sql

-- changeset john:1750565255493-8
ALTER TABLE role_organize_relation_entity DROP FOREIGN KEY FK1jmylyd0kcod3u8jw5nmmyyn8;

-- changeset john:1750565255493-9
ALTER TABLE role_permission_relation_entity DROP FOREIGN KEY FK2o4r4d77pv4yuwn9uhfg05usa;

-- changeset john:1750565255493-10
ALTER TABLE role_permission_relation_entity DROP FOREIGN KEY FKfkobmpfsitu0k0r20u6w34kej;

-- changeset john:1750565255493-11
ALTER TABLE role_organize_relation_entity DROP FOREIGN KEY FKn2ghw3do41s32ttv67rqq3i6o;

-- changeset john:1750565255493-1
CREATE TABLE permission_relation_entity (id VARCHAR(255) NOT NULL, create_date datetime(6) NOT NULL, update_date datetime(6) NOT NULL, organize_id VARCHAR(255) NULL, permission_id VARCHAR(255) NOT NULL, role_id VARCHAR(255) NOT NULL, CONSTRAINT PK_PERMISSION_RELATION_ENTITY PRIMARY KEY (id));

-- changeset john:1750565255493-2
ALTER TABLE permission_relation_entity ADD CONSTRAINT UKaboxfwkak41dni436dqhbsucg UNIQUE (role_id, permission_id, organize_id);

-- changeset john:1750565255493-3
CREATE INDEX FK3l6w4agmyw25tyufjqnaw0r36 ON permission_relation_entity(organize_id);

-- changeset john:1750565255493-4
CREATE INDEX FKcjnmeu2avfsc9wk862kyq7mcw ON permission_relation_entity(permission_id);

-- changeset john:1750565255493-5
ALTER TABLE permission_relation_entity ADD CONSTRAINT FK3l6w4agmyw25tyufjqnaw0r36 FOREIGN KEY (organize_id) REFERENCES organize_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset john:1750565255493-6
ALTER TABLE permission_relation_entity ADD CONSTRAINT FKcjnmeu2avfsc9wk862kyq7mcw FOREIGN KEY (permission_id) REFERENCES permission_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset john:1750565255493-7
ALTER TABLE permission_relation_entity ADD CONSTRAINT FKuykplwf8ej7aeiiksirgc6o7 FOREIGN KEY (role_id) REFERENCES role_entity (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset john:1750565255493-12
ALTER TABLE role_permission_relation_entity DROP KEY UKcdovbasr36jrwjhj6tycl4v6a;

-- changeset john:1750565255493-13
ALTER TABLE role_organize_relation_entity DROP KEY UKhcg7w3c9c8u5j8kmr2q9roial;

-- changeset john:1750565255493-14
DROP TABLE role_organize_relation_entity;

-- changeset john:1750565255493-15
DROP TABLE role_permission_relation_entity;

-- changeset john:1750565255493-16
ALTER TABLE role_entity DROP COLUMN deletion_code;

-- changeset john:1750565255493-17
ALTER TABLE role_entity DROP COLUMN is_organize_role;

