--liquibase formatted sql

--changeset ds:1
CREATE TABLE reminder_task
(
    id        BIGSERIAL PRIMARY KEY,
    chat_id   BIGINT    NOT NULL,
    message   TEXT      NOT NULL,
    date_time TIMESTAMP NOT NULL

);
--changeset ds:2
CREATE INDEX IF NOT EXISTS idx_reminder_chat_id ON reminder_task (chat_id);