-- liquibase formatted sql

-- changeset john:1748606717567-1
ALTER TABLE encrypt_decrypt_entity ADD name VARCHAR(255) NOT NULL;

-- changeset john:1748606717567-2
ALTER TABLE encrypt_decrypt_entity ADD CONSTRAINT UKe52bfnsumtdjvrx6nyj872cop UNIQUE (name);

