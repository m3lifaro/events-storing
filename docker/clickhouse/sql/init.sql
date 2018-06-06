CREATE DATABASE IF NOT EXISTS test;

CREATE TABLE IF NOT EXISTS test.events
(
    event_date Date,
    event_time DateTime,
    params Nested (
        name String,
        string_value String,
        double_value Float64
    )
) ENGINE = MergeTree PARTITION BY toMonday(event_date) ORDER BY (app_id, platform, event_type, event_date);
