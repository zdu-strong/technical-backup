-- liquibase formatted sql

-- changeset john:1746872538195-1
ALTER TABLE distributed_execution_main_entity ADD total_pages BIGINT NOT NULL;

-- changeset john:1746872538195-2
ALTER TABLE distributed_execution_main_entity ADD total_records BIGINT NOT NULL;

-- changeset john:1746872538195-3
ALTER TABLE distributed_execution_main_entity DROP COLUMN total_page;

