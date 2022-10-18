DROP TABLE IF exists endpoints_hit CASCADE;
CREATE TABLE IF NOT EXISTS endpoints_hit (
                               id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL ,
                               app VARCHAR(255),
                               uri VARCHAR(255),
                               ip VARCHAR(255),
                               timestamp TIMESTAMP,
                               CONSTRAINT pk_endpoints PRIMARY KEY (id)
)